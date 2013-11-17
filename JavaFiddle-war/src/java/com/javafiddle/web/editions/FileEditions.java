package com.javafiddle.web.editions;

import java.util.Date;
import java.util.TreeMap;

public class FileEditions {
    TreeMap<Date, String> editions = new TreeMap<>();
    
    public int size() {
        return editions.size();
    }

    public void addRevision(Date timeStamp, String text) {
        editions.put(timeStamp, text);
    }
    
    public String getByTimeStamp(Date timeStamp) {
        if (editions.containsKey(timeStamp))
            return editions.get(timeStamp);
        return null;
    }
    
    public String getLastRevision() {
        if (editions.isEmpty())
            return null;
        return editions.lastEntry().getValue();
    }
}