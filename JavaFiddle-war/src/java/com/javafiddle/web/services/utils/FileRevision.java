package com.javafiddle.web.services.utils;

public class FileRevision {
    long timeStamp;
    String value;

    public FileRevision() {
    }

    public FileRevision(long timeStamp, String value) {
        this.timeStamp = timeStamp;
        this.value = value;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long time) {
        this.timeStamp = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String valueStamp) {
        this.value = valueStamp;
    }
}
