package com.vipin.productMicroservice.model;

import com.vipin.productMicroservice.dto.ProductRequest;
import jakarta.persistence.*;

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