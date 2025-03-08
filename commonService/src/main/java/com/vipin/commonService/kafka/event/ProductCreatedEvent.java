package com.vipin.commonService.kafka.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}