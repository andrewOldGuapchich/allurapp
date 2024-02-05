package com.example.allur_app.model.product;

public class ScanProduct {
    private String id;
    private int count;
    private boolean isSelect;

    public ScanProduct(String id, int count, boolean isSelect) {
        this.id = id;
        this.count = count;
        this.isSelect = isSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
