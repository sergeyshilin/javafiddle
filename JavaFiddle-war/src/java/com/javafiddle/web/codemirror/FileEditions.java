package com.javafiddle.web.codemirror;

import java.util.ArrayList;
import java.util.List;

public class FileEditions {
    List<Dummy> editions;
    int currentindex, maxindex;
    
    public FileEditions() {
        editions = new ArrayList<>();
        currentindex = -1;
        maxindex = -1;
    }
    
    public int size() {
        return editions.size();
    }

    public void addRevision(Dummy newElement) {
            editions.add(newElement);
            maxindex = editions.lastIndexOf(newElement);
            currentindex = maxindex;
    }

    public Dummy getLastRevision() {
        if (maxindex == -1)
            return null;
        return editions.get(maxindex);
    }
    
    public Dummy getCurrentRevision() {
        if (currentindex == -1)
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
        if (currentindex < maxindex)
            return true;
        return false;               
    }   
}