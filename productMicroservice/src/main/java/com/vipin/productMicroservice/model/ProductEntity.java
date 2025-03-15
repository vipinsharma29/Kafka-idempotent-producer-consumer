package com.vipin.productMicroservice.model;

import com.vipin.productMicroservice.dto.ProductRequest;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "productId")
    private Integer id;

    @Column(name = "productName")
    private String title;

    @Column(name = "price")
    private Double price;

    @Column(name = "productQuantity")
    private Integer quantity;

    public ProductEntity(ProductRequest productRequest) {
        this.id = null;
        this.title = productRequest.getTitle();
        this.price = productRequest.getPrice();
        this.quantity = productRequest.getQuantity();
    }
}