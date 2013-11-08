package com.javafiddle.web.tree;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TreePackage implements Comparable<TreePackage>{
    private String name;
    private int id;
    private int parentId;
    private int parents;
    private List<TreeFile> files = new ArrayList<>();
    
    public TreePackage(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
        
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getParents() {
        return parents;
    }

    public void setParents(int parents) {
        this.parents = parents;
    }
    
    public TreeFile addFile(String type, String name) {
        TreeFile tf = new TreeFile(name, type, "");
        files.add(tf);
        tf.setId(IdList.getInstance().addId(tf));
        tf.setPackageId(id);
        Collections.sort(files);
        return tf;
    }
        
    public void deleteFile(TreeFile tf) {
        files.remove(tf);
    }
    
    @Override
    public int compareTo(TreePackage compareObject)
    {
        if (parents < compareObject.getParents())
            return -1;
        else if (parents == compareObject.getParents()) {
            Collator collator = Collator.getInstance(new Locale("en", "US"));
            return collator.compare(name, compareObject.name);
        } else
            return 1;
    }

}