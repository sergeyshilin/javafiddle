package com.javafiddle.web.tree;

import java.io.Serializable;
import java.text.Collator;
import java.util.Date;
import java.util.Locale;

public class TreeFile implements Comparable<TreeFile>, Serializable { 
    private int id;
    private int packageId;
    private String name;
    private String type;
    private Date timeStamp;

    public TreeFile(String name, String type) {
        this.name = name;
        this.type = type;
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(TreeFile compareObject) {
        Collator collator = Collator.getInstance(new Locale("en", "US"));
        return collator.compare(name, compareObject.name);
    }
}
