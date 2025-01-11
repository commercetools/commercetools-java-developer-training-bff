package com.training.handson.dto;

import java.util.Map;

public class CustomObjectRequest {

    private String container;
    private String key;
    private Map<String, Object> jsonObject;

    public Map<String, Object> getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(Map<String, Object> jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }
}
