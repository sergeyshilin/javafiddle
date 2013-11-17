package com.javafiddle.web.services.utils;

public class FileRevision {
    String timeStamp;
    String value;

    public FileRevision() {
    }

    public FileRevision(String timeStamp, String value) {
        this.timeStamp = timeStamp;
        this.value = value;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String time) {
        this.timeStamp = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String valueStamp) {
        this.value = valueStamp;
    }
}
