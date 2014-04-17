package com.javafiddle.tree;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class TreePackage implements TreeNode, Comparable<TreePackage>, Serializable {
    private final IdNodeType nodeType = IdNodeType.PACKAGE;
    private String name;
    private String shortName;
    private long id;
    private long parentId;
    private long parents;
    private long projectId;
    private List<TreeClass> files = new ArrayList<>();
    
    public TreePackage(String name) {
        this.name = name;
    }

    @Override
    public IdNodeType getNodeType() {
        return nodeType;
    }
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
        
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    
    public long getParents() {
        return parents;
    }

    public void setParents(long parents) {
        this.parents = parents;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public List<TreeClass> getFiles() {
        return files;
    }

    public TreeClass addFile(IdList idList, String type, String name) {
        TreeClass tf = new TreeClass(name, type);
        files.add(tf);
        tf.setId(idList.add(tf));
        tf.setPackageId(id);
        Collections.sort(files);
        return tf;
    }
        
    public TreeClass getFile(String name) {
        for (TreeClass temp : files)
            if (name.equals(temp.getName()))
                    return temp;
        return null;
    }
    
    public void deleteFile(IdList idList, int fileId) {
        files.remove(idList.getClass(fileId));
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
        sb.append("\"name\"").append(":\"").append(shortName == null ? name : shortName).append("\", ");   
        sb.append("\"parentId\"").append(":\"").append(parentId).append("\", ");    
        sb.append("\"files\"").append(":").append("[");
        for (TreeClass entry : files)
            sb.append(entry.toJSON()).append(", ");
        if (!files.isEmpty())
            sb.delete(sb.length()-2, sb.length());
        sb.append("]").append("}");
        return sb.toString();
    }
    
    public TreeMap<Long, TreeNode> getIdList() {
        TreeMap<Long, TreeNode> idList = new TreeMap<>();
        for (TreeClass entry : files)
            idList.put(entry.getId(), entry);
        return idList;
    }
}