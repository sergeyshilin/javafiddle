package com.javafiddle.web.tree;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TreePackage implements TreeNode, Comparable<TreePackage>, Serializable {
    private final IdNodeType nodeType = IdNodeType.PACKAGE;
    private String name;
    private int id;
    private int parentId;
    private int parents;
    private int projectId;
    private List<TreeFile> files = new ArrayList<>();
    
    public TreePackage(String name) {
        this.name = name;
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
        
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
    public int getParents() {
        return parents;
    }

    public void setParents(int parents) {
        this.parents = parents;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public List<TreeFile> getFiles() {
        return files;
    }

    public void setFiles(List<TreeFile> files) {
        this.files = files;
    }
    
    public TreeFile addFile(IdList idList, String type, String name) {
        TreeFile tf = new TreeFile(name, type);
        files.add(tf);
        tf.setId(idList.add(tf));
        tf.setPackageId(id);
        Collections.sort(files);
        return tf;
    }
        
    public void deleteFile(IdList idList, int fileId) {
        files.remove(idList.getFile(fileId));
        idList.remove(fileId);
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

    @Override
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\"").append(":").append(id).append(", ");
        sb.append("\"name\"").append(":\"").append(name).append("\", ");   
        sb.append("\"parentId\"").append(":\"").append(parentId).append("\", ");    
        sb.append("\"files\"").append(":").append("[");
        for (TreeFile entry : files)
            sb.append(entry.toJSON()).append(", ");
        sb.delete(sb.length()-2, sb.length());
        sb.append("]").append("}");
        return sb.toString();
    }
}