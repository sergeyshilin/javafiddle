package com.javafiddle.web.codemirror;

import java.util.ArrayList;
import java.util.List;
import org.primefaces.context.RequestContext;

public class FileEditions {
    List<String> editions;
    int currentindex, maxindex;
    String current;
    int returnedIndex;
    
    public FileEditions() {
        editions = new ArrayList<>();
        currentindex = -1;
        maxindex = -1;
        current = "\"\""; 
        returnedIndex = -1;
    }

    public void setCurrent(String current) {
        if ((maxindex < 0 || !current.equals(editions.get(maxindex))) &&
           (returnedIndex == -1 || !current.equals(editions.get(returnedIndex))) &&
           (returnedIndex == maxindex || !current.equals(editions.get(returnedIndex+1)))){
            editions.add(current);
            if (editions.lastIndexOf(current) > 19)
                editions.remove(0);
            maxindex = editions.lastIndexOf(current);
            currentindex = maxindex;
            returnedIndex = -1;
        }
    }

    public String getCurrent() {
        if (maxindex == -1)
            return "\"\"";
        return editions.get(currentindex);
    }
    
    public void inc() {
        if (hasNext()) {
            currentindex++;
            returnedIndex = currentindex;
            RequestContext context = RequestContext.getCurrentInstance(); 
            context.update("hiddenButton");
        }
    }
    
    public void dec() {
        if (hasPrevious()) {
            currentindex--;
            returnedIndex = currentindex;
            RequestContext context = RequestContext.getCurrentInstance(); 
            context.update("hiddenButton");
        }
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