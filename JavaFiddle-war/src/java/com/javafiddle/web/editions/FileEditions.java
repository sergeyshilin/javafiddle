package com.javafiddle.web.editions;

import com.javafiddle.web.services.structuresforjson.AddFileRevisionRequest;
import java.util.ArrayList;
import java.util.List;

public class FileEditions {
    List<AddFileRevisionRequest> editions = new ArrayList<>();
    int currentindex = 0;
    
    public int size() {
        return editions.size();
    }

    public void addRevision(AddFileRevisionRequest newElement) {
            editions.add(newElement);
            currentindex = editions.size()-1;
    }

    public AddFileRevisionRequest getLastRevision() {
        if (editions.isEmpty())
            return null;
        return editions.get(editions.size()-1);
    }
    
    public AddFileRevisionRequest getCurrentRevision() {
        if (editions.isEmpty())
            return null;
        return editions.get(currentindex);
    }
    
    public AddFileRevisionRequest getPrevRevision() {
        if (hasPrevious())
            currentindex--;
        return editions.get(currentindex);
    }
   
    public AddFileRevisionRequest getNextRevision() {
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