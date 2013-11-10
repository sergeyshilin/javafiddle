package com.javafiddle.web.services.structuresforjson;

public class AddFileRevisionRequest {
    private String id;
    private String time;
    private String value;

    public AddFileRevisionRequest() {
    }
    
    public AddFileRevisionRequest(String id, String time, String value) {
        this.id = id;
        this.time = time;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
}
