package com.javafiddle.web.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeProject {
    private String name;
    private int id;
    private List<TreePackage> packages = new ArrayList<>();
    
    public TreeProject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
        
    public void setName(String name) {
        this.name = name;
    }
        
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public TreePackage getPackage(String name) {
        TreePackage tp = null;
        for (TreePackage temp : packages)
            if (name.equals(temp.getName()))
                tp = temp;
        if (tp == null) {
            tp = new TreePackage(name);
            packages.add(tp);
            tp.setId(IdList.getInstance().addId(tp));
            calcParents();
        }
        return tp;
    }
    
    private void calcParents() {
        for (int i = 0; i < packages.size(); i++) {
            packages.get(i).setParentId(id);
            String current = packages.get(i).getName();
            while (!current.equals("")) {
                current = current.substring(0, Math.max(current.lastIndexOf('.'), 0));
                for (int j = 0; j < packages.size(); j++)
                    if (current.equals(packages.get(j).getName())) {
                        packages.get(i).setParentId(packages.get(j).getId());
                        packages.get(i).setParents(packages.get(j).getParents() + 1);
                        current = "";
                        break;
                    }
            }
        }
        Collections.sort(packages);
    }
}