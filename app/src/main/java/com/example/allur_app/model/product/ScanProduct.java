package com.example.allur_app.model.product;

public class ScanProduct {
    private long id;
    private int count;
    private boolean isSelect;

    public ScanProduct(long id, int count, boolean isSelect) {
        this.id = id;
        this.count = count;
        this.isSelect = isSelect;
    }

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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
