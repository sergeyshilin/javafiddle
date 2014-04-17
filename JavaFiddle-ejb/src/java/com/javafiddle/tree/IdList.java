package com.javafiddle.tree;

import com.javafiddle.core.ejb.util.IdGeneratorLocal;
import java.io.Serializable;
import java.util.HashMap;
import javax.ejb.EJB;

public class IdList extends HashMap<Long, TreeNode> implements Serializable {
    
    @EJB
    private IdGeneratorLocal idGenerator;
        
    public long add(TreeNode treeNode) {
        long id = idGenerator.getNextId();
        treeNode.setId(id);
        System.out.println("id = " + id);
        
        return id;  
    }
    
    public boolean isExist(long id) {
        return get(id) != null;   
    }
    
    public IdNodeType getType(long id) {
        return get(id).getNodeType();
    }
    
    public boolean isProject(long id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.PROJECT;
        return false;
    } 
    
    public boolean isPackage(long id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.PACKAGE;
        return false;
    } 
    
    public boolean isClass(long id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.CLASS;
        return false;
    } 
    
    public TreeProject getProject(long id) {
        if (isProject(id))
            return (TreeProject)get(id);
        return null;
    }
    
    public TreePackage getPackage(long id) {
        System.out.println(get(id).getNodeType());
        if (isPackage(id))
            return (TreePackage)get(id);
        return null;
    }
    
    public TreeClass getClass(long id) {
        if (isClass(id))
            return (TreeClass)get(id);
        return null;
    } 
    
    public HashMap<Long, TreeClass> getFileList() {
        HashMap<Long, TreeClass> fileList = new HashMap<>();
        for (long id : this.keySet())
            if (isClass(id))
                fileList.put(id, getClass(id));
        return fileList;
    }
}

