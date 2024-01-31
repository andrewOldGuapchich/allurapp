package com.example.allur_app.model.product;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ProductInform {
    @JsonProperty("name")
    private String name;
    @JsonProperty("productInforms")
    private List<Product> productInforms;

    public ProductInform() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProductInforms() {
        return productInforms;
    }

    public void setProductInforms(List<Product> productInforms) {
        this.productInforms = productInforms;
    }
}
