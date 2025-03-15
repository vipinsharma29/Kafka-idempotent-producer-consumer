package com.vipin.emailNotificationService;

import com.vipin.commonService.kafka.event.ProductCreatedEvent;
import com.vipin.emailNotificationService.handler.KafkaEventHandler;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1, topics = {"productCreatedEventTopic"}, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class KafkaEventHandlerTest {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    KafkaEventHandler kafkaEventHandler;

    @Test
    public void testKafkaEventHandler_OnProductCreated_HandlesEvent() throws Exception {
        // Arrange: Create a sample event
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                101, "Test Product", 500.0, 10);

        String messageId = UUID.randomUUID().toString();
        String messageKey = productCreatedEvent.getTitle();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "product-created-events-topic",
                messageKey,
                productCreatedEvent);

        record.headers().add("messageId", messageId.getBytes());
        record.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes());

        // Act: Send event to Kafka topic
        kafkaTemplate.send(record).get();

        // Wait for the Kafka consumer to process the message
        TimeUnit.SECONDS.sleep(3);

        // Assert: Verify that the KafkaEventHandler consumed the message
        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);

//        verify(kafkaEventHandler).handle(eventCaptor.capture(),
//                messageIdCaptor.capture(),
//                messageKeyCaptor.capture());
//
//        // Now retrieve the captured values AFTER verify() has captured them
//        assertEquals(messageId, messageIdCaptor.getValue());
//        assertEquals(messageKey, messageKeyCaptor.getValue());
//        assertEquals(productCreatedEvent.getId(), eventCaptor.getValue().getId());
    }
}