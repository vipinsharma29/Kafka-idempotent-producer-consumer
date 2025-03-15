package com.vipin.commonService.kafka.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductCreatedEvent {

    private Integer id;

    private String title;

    private Double price;

    private Integer quantity;

    @JsonCreator
    public ProductCreatedEvent(
            @JsonProperty("id") Integer id,
            @JsonProperty("title") String title,
            @JsonProperty("price") Double price,
            @JsonProperty("quantity") Integer quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }
}