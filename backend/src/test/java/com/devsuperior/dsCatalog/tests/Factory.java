package com.devsuperior.dsCatalog.tests;

import java.time.Instant;

import com.devsuperior.dsCatalog.dto.ProductDTO;
import com.devsuperior.dsCatalog.entities.Category;
import com.devsuperior.dsCatalog.entities.Product;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good phone", 800.0, "https://img.com/img.png", Instant.parse("2020-10-10T10:10:10Z"));
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
