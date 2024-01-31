package com.example.allur_app.model.http;

public class ApiResponse<T> {
    private int status;
    private T body;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public ApiResponse(int status, T body) {
        this.status = status;
        this.body = body;
    }
}
