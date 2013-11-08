package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class IdList implements Serializable {
    private static IdList instance;
    private List<Object> idList = new ArrayList<>();
    private List<Integer> removedNodes = new ArrayList<>();
    
    private IdList() {
    }
 
    public static IdList getInstance() {
        if (instance == null)
            instance = new IdList();
        return instance;
    }
    
    public int addId(Object object) {
        if (idList.indexOf(object) != -1)
            return -1;
        if (!removedNodes.isEmpty()) {
            idList.add(removedNodes.get(0), object);
            removedNodes.remove(0);
        } else
            idList.add(object);
        return idList.indexOf(object);              
    }
    
    public void removeId(int id) {
        if (idList.get(id) != null) {
            idList.add(id, null);
            removedNodes.add(id);
        }
    }
    
    public Object getById(int id) {
        return idList.get(id);
    }
}

