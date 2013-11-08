package com.javafiddle.web.codemirror;

import java.util.ArrayList;
import java.util.List;

public class FileEditions {
    List<Dummy> editions = new ArrayList<>();
    int currentindex = 0;
    
    public int size() {
        return editions.size();
    }

    public void addRevision(Dummy newElement) {
            editions.add(newElement);
            currentindex = editions.size()-1;
    }

    public Dummy getLastRevision() {
        if (editions.isEmpty())
            return null;
        return editions.get(editions.size()-1);
    }
    
    public Dummy getCurrentRevision() {
        if (editions.isEmpty())
            return null;
        return editions.get(currentindex);
    }
    
    public Dummy getPrevRevision() {
        if (hasPrevious())
            currentindex--;
        return editions.get(currentindex);
    }
   
    public Dummy getNextRevision() {
        if (hasNext())
            currentindex++;
        return editions.get(currentindex);
    }
    
    public boolean hasPrevious() {
        if (currentindex > 0)
            return true;
        return false;       
    }
    
    public boolean hasNext() {
        if (currentindex < editions.size()-1)
            return true;
        return false;               
    }   
}