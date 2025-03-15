package com.vipin.productMicroservice.dto;

import lombok.Data;

@Data
public class ProductRequest {

    private String title;
    private Double price;
    private Integer quantity;
}