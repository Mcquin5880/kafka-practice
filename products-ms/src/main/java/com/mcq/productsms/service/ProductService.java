package com.mcq.productsms.service;

import com.mcq.productsms.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public String createProduct(ProductDto productDto) {
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(productId)
                .title(productDto.getTitle())
                .price(productDto.getPrice())
                .quantity(productDto.getQuantity())
                .build();

        var future = kafkaTemplate.send("product-created-events-topic", productId, event);
        future.whenComplete((result, exception) -> {
           if (exception != null) {
               log.error("Failed to send message: {}", exception.getMessage());
           } else {
               log.info("Message sent successfully: {}", result.getRecordMetadata());
           }
        });

        return productId;
    }

    // synchronous example

    public String createProductSynchronous(ProductDto productDto) throws Exception {
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(productId)
                .title(productDto.getTitle())
                .price(productDto.getPrice())
                .quantity(productDto.getQuantity())
                .build();

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send("product-created-events-topic", productId, event).get();
        log.info("Partition: {}", result.getRecordMetadata().partition());
        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Offset: {}", result.getRecordMetadata().offset());

        return productId;
    }
}
