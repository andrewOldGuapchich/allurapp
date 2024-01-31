package com.example.allur_app.model.box;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoxInformEntity {
    @JsonProperty("productId")
    private long id;
    @JsonProperty("count")
    private int count;
    @JsonProperty("description")
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
