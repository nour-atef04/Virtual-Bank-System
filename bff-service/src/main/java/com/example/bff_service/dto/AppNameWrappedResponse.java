package com.example.bff_service.dto;

public class AppNameWrappedResponse<T> {
    private String appName;
    private T data;

    public AppNameWrappedResponse(String appName, T data) {
        this.appName = appName;
        this.data = data;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
