package com.javafiddle.web.services.utils;

public class AddFileRevisionRequest extends FileRevision{
    private String id;

    public AddFileRevisionRequest(String id, String time, String value) {
        this.id = id;
        super.timeStamp = time;
        super.value = value;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
