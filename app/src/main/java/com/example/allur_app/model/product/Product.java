package com.example.allur_app.model.product;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    @JsonProperty("boxId")
    private long boxId;
    @JsonProperty("count")
    private int count;

    public Product() {
    }


    public long getBoxId() {
        return boxId;
    }

    public void setBoxId(long boxId) {
        this.boxId = boxId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
