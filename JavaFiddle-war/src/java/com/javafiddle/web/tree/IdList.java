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
    
    public TreeProject getProject(int id) {
        if (idList.get(id).getIdNodeType() == IdNodeType.PROJECT)
            return (TreeProject)idList.get(id).getObject();
        return null;
    }
    
    public TreePackage getPackage(int id) {
        if (idList.get(id).getIdNodeType() == IdNodeType.PACKAGE)
            return (TreePackage)idList.get(id).getObject();
        return null;
    }
    
    public TreeFile getFile(int id) {
        if (idList.get(id).getIdNodeType() == IdNodeType.FILE)
            return (TreeFile)idList.get(id).getObject();
        return null;
    }
}

