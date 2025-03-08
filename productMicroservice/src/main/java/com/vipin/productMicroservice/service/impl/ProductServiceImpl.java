package com.vipin.productMicroservice.service.impl;

import com.vipin.commonService.kafka.event.ProductCreatedEvent;
import com.vipin.productMicroservice.dto.ProductRequest;
import com.vipin.productMicroservice.dto.ProductResponse;
import com.vipin.productMicroservice.model.ProductEntity;
import com.vipin.productMicroservice.repository.ProductRepository;
import com.vipin.productMicroservice.service.ProductService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Override
    public ProductResponse createProduct(ProductRequest product) {
        try {
            ProductEntity savedData = productRepository.save(new ProductEntity(product));
            if (savedData.getId() != null) {
                ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(savedData.getId(), savedData.getTitle(), savedData.getPrice(), savedData.getQuantity());
                ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                        "productCreatedEventTopic", savedData.getId().toString(), productCreatedEvent);
                record.headers().add("messageId", savedData.getId().toString().getBytes());
                CompletableFuture<SendResult<String, ProductCreatedEvent>> kafkaFuture =
                        kafkaTemplate.send(record);
                kafkaFuture.whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("****** Failed to send message: {} ****** ", exception.getMessage());
                    } else {
                        log.info("****** Message sent successfully: {} ****** ", result.getRecordMetadata());
                    }
                });
//                kafkaFuture.join(); //if need to wait and check error or message saved in kafka or not
            }
            return new ProductResponse(savedData);
        } catch (Exception e) {
            log.error("****** Exception while saving Product to database: {} ****** ", e.getMessage());
            return null;
        }
    }
}