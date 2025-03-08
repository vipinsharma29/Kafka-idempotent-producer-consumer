package com.vipin.productMicroservice.dto;

import com.vipin.productMicroservice.model.ProductEntity;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ProductResponse {

    private int id;
    private String title;
    private Double price;
    private Integer quantity;

    public ProductResponse(int id, String title, Double price, Integer quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductResponse(ProductEntity product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
    }
}