package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IdList extends HashMap<Integer, TreeNode> implements Serializable {
    private int count = 0;

    protected int add(TreeNode treeNode) {
        put(count++, treeNode);
        return count-1;              
    }
    
    public boolean isExist(int id) {
        if (get(id) == null)
            return false;
        return true;   
    }
    
    public IdNodeType getType(int id) {
        return get(id).getNodeType();
    }
    
    public boolean isProject(int id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.PROJECT;
        return false;
    } 
    
    public boolean isPackage(int id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.PACKAGE;
        return false;
    } 
    
    public boolean isFile(int id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.FILE;
        return false;
    } 
    
    public TreeProject getProject(int id) {
        if (isProject(id))
            return (TreeProject)get(id);
        return null;
    }
    
    public TreePackage getPackage(int id) {
        if (isPackage(id))
            return (TreePackage)get(id);
        return null;
    }
    
    public TreeFile getFile(int id) {
        if (isFile(id))
            return (TreeFile)get(id);
        return null;
    } 
    
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<Integer, TreeNode> entry : entrySet())
            sb.append(entry.getKey()).append(":").append(entry.getValue().getName()).append(", ");
        sb.delete(sb.length()-2, sb.length());
        sb.append("}");
        return sb.toString();
    }
}

