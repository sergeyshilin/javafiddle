package com.javafiddle.web.editions;

import com.javafiddle.web.tree.TreeProject;
import java.util.ArrayList;
import java.util.List;

public class ProjectEditions {
    List<TreeProject> editions = new ArrayList<>();
    int currentindex;
    
    public void addRevision(TreeProject newElement) {
            editions.add(newElement);
            currentindex = editions.size();
    }

    public TreeProject getLastRevision() {
        if (editions.isEmpty())
            return null;
        return editions.get(editions.size());
    }
}
