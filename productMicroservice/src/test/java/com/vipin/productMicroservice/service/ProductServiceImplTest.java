package com.vipin.productMicroservice.service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vipin.commonService.kafka.event.ProductCreatedEvent;
import com.vipin.productMicroservice.dto.ProductRequest;

import com.vipin.productMicroservice.service.impl.ProductServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext // Optional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// by default junit create new instance of this class before executing each test method means 1 instance for each test method
@ActiveProfiles("test") // application-test.properties
@EmbeddedKafka(partitions = 2, count = 1, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    Environment environment;

    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;
    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> records;

    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, ProductCreatedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(getConsumerProperties());

        String topicName = environment.getProperty("product-created-events-topic-name");
        Assertions.assertNotNull(topicName, "Kafka topic name must be set in application-test.properties");

        ContainerProperties containerProperties = new ContainerProperties(topicName);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>) records::add);

        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void testCreatesProduct() throws Exception {

        ProductRequest productRequest = new ProductRequest();
        productRequest.setPrice(1200.0);
        productRequest.setQuantity(12);
        productRequest.setTitle("test");

        productService.createProduct(productRequest);

        ConsumerRecord<String, ProductCreatedEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message, "Kafka message should not be null");
        assertNotNull(message.key(), "Kafka message key should not be null");

        ProductCreatedEvent productCreatedEvent = message.value();
        assertNotNull(productCreatedEvent, "ProductCreatedEvent should not be null");
        assertEquals(productRequest.getQuantity(), productCreatedEvent.getQuantity(), "Quantity should match");
        assertEquals(productRequest.getTitle(), productCreatedEvent.getTitle(), "Title should match");
        assertEquals(productRequest.getPrice(), productCreatedEvent.getPrice(), "Price should match");
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset")
        );
    }

    @AfterAll
    void tearDown() {
        if (container != null) {
            container.stop();
        }
    }

}
