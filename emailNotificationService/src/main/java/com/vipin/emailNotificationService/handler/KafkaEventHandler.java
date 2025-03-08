package com.vipin.emailNotificationService.handler;

import com.vipin.commonService.kafka.event.ProductCreatedEvent;
import com.vipin.emailNotificationService.exception.NotRetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
//@KafkaListener(topics = "productCreatedEventTopic", groupId = "productCreatedEvents")
public class KafkaEventHandler {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventHandler.class);

    @KafkaListener(topics = "productCreatedEventTopic")
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent, @Header(value = "messageId", required = true) String messageId,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey) {
        try {
            log.info("****** Received new message from kafka: {} ******", productCreatedEvent.getTitle());
        } catch (Exception e) {
            throw new NotRetryableException("NotRetryableException");
        }
    }

}