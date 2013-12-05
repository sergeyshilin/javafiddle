package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IdList extends HashMap<Integer, TreeNode> implements Serializable {
    private int count = 0;

    protected int add(TreeNode treeNode) {
        super.put(count++, treeNode);
        return count-1;  
    }
    
    @Override
    public TreeNode put(Integer key, TreeNode value) {
        add(value);
        return null;
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
    
    public HashMap<Integer, TreeFile> getFileList() {
        HashMap<Integer, TreeFile> fileList = new HashMap<>();
        for (int id : this.keySet())
            if (isFile(id))
                fileList.put(id, getFile(id));
        return fileList;
    }
}

