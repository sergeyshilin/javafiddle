package com.javafiddle.web.codemirror;

import java.util.ArrayList;
import java.util.List;

public class ProjectEditions {
    List<Tree> editions;
    int currentindex, maxindex;
    
public ProjectEditions() {
        editions = new ArrayList<>();
        currentindex = -1;
        maxindex = -1;
    }

    public void addRevision(Tree newElement) {
            editions.add(newElement);
            maxindex = editions.lastIndexOf(newElement);
            currentindex = maxindex;
    }

    public Tree getLastRevision() {
        if (maxindex == -1)
            return null;
        return editions.get(maxindex);
    }
}
