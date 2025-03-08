package com.vipin.productMicroservice.service;

import com.vipin.productMicroservice.dto.ProductRequest;
import com.vipin.productMicroservice.dto.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest product);
}