package com.javafiddle.web.tree;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class TreeFile implements TreeNode, Comparable<TreeFile>, Serializable { 
    private final IdNodeType nodeType = IdNodeType.FILE;
    private int id;
    private int packageId;
    private String name;
    private String type;
    private long timeStamp;

    public TreeFile(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public IdNodeType getNodeType() {
        return nodeType;
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(TreeFile compareObject) {
        Collator collator = Collator.getInstance(new Locale("en", "US"));
        return collator.compare(name, compareObject.name);
    }
    
    @Override
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\"").append(":").append(id).append(", ");
        sb.append("\"name\"").append(":\"").append(name).append("\", ");      
        sb.append("\"type\"").append(":\"").append(type).append("\"");
        sb.append("}");
        return sb.toString();
    }
}
