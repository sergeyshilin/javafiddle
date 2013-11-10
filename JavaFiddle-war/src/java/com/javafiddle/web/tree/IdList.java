package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IdList implements Serializable {

    private List<IdListElement> idList = new ArrayList<>();
    private List<Integer> removedNodes = new ArrayList<>();
    
    protected int addId(TreeProject project) {
        return addObject(IdNodeType.PROJECT, (Object)project);
    }
    
    protected int addId(TreePackage pack) {
        return addObject(IdNodeType.PACKAGE, pack);
    }
        
    protected int addId(TreeFile file) {
        return addObject(IdNodeType.FILE, file);
    }
    
    protected int addObject(IdNodeType type, Object object) {
        if (idList.indexOf(object) != -1)
            return -1;
        IdListElement ile = new IdListElement(type, object);
        if (!removedNodes.isEmpty()) {
            idList.set(removedNodes.get(0), ile);
            removedNodes.remove(0);
        } else
            idList.add(ile);
        return idList.indexOf(ile);              
    }
    
    protected void removeId(int id) {
        if (idList.get(id) != null) {
            idList.add(id, null);
            removedNodes.add(id);
        }
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

