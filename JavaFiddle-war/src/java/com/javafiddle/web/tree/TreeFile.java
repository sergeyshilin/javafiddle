package com.javafiddle.web.tree;

import java.text.Collator;
import java.util.Locale;

public class TreeFile implements Comparable<TreeFile> { 
    private String name;
    private int id;
    private int packageId;
    private String type;
    private String timeStamp;

    public TreeFile(String name, String type, String timeStamp) {
        this.name = name;
        this.type = type;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(TreeFile compareObject) {
        Collator collator = Collator.getInstance(new Locale("en", "US"));
        return collator.compare(name, compareObject.name);
    }
}
