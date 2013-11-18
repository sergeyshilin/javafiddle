package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IdList implements Serializable {
    private Map<Integer, IdListElement> idList = new HashMap<>();
    private int count = 0;
    
    protected int addId(TreeProject project) {
        return addObject(IdNodeType.PROJECT, project);
    }
    
    protected int addId(TreePackage pack) {
        return addObject(IdNodeType.PACKAGE, pack);
    }
        
    protected int addId(TreeFile file) {
        return addObject(IdNodeType.FILE, file);
    }
    
    protected int addObject(IdNodeType type, Object object) {
        IdListElement ile = new IdListElement(type, object);
        if (idList.containsValue(ile))
            return -1;
        idList.put(count++, ile);
        return count-1;              
    }
    
    protected void removeId(int id) {
        idList.remove(id);
    }
    
    public boolean isExist(int id) {
        if (idList.get(id) == null)
            return false;
        return true;   
    }
    
    public IdNodeType getType(int id) {
        return idList.get(id).getIdNodeType();
    }
    
    public boolean contains(int id) {
        return idList.containsKey(id);
    }
    
    public boolean isProject(int id) {
        if (contains(id))
            return getType(id) == IdNodeType.PROJECT;
        return false;
    } 
    
    public boolean isPackage(int id) {
        if (contains(id))
            return getType(id) == IdNodeType.PACKAGE;
        return false;
    } 
    
    public boolean isFile(int id) {
        if (contains(id))
            return getType(id) == IdNodeType.FILE;
        return false;
    } 
    
    public TreeProject getProject(int id) {
        if (isProject(id))
            return (TreeProject)idList.get(id).getObject();
        return null;
    }
    
    public TreePackage getPackage(int id) {
        if (isPackage(id))
            return (TreePackage)idList.get(id).getObject();
        return null;
    }
    
    public TreeFile getFile(int id) {
        if (isFile(id))
            return (TreeFile)idList.get(id).getObject();
        return null;
    } 
}

