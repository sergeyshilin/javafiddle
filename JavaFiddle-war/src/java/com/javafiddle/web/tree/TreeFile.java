package com.javafiddle.web.tree;

import java.text.Collator;
import java.util.Locale;

public class TreeFile implements Comparable<TreeFile> { 
    private String name;
    private int id;
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

    public String getTimeRevision() {
        return timeStamp;
    }

    public void setTimeRevision(String timeRevision) {
        this.timeStamp = timeRevision;
    }

    @Override
    public int compareTo(TreeFile compareObject) {
        Collator collator = Collator.getInstance(new Locale("en", "US"));
        return collator.compare(name, compareObject.name);
    }
}
