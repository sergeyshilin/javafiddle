package com.javafiddle.web.tree;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TreeProject implements Comparable<TreeProject>, Serializable {
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

    public List<TreePackage> getPackages() {
        return packages;
    }

    public void setPackages(List<TreePackage> packages) {
        this.packages = packages;
    }
    
    public TreePackage getPackageInstance(IdList idList, String name) {
        return addPackage(idList, name, true);
    }
    
    public TreePackage addPackage(IdList idList, String name) {
        return addPackage(idList, name, false);
    }
    
    public TreePackage addPackage(IdList idList, String name, boolean getInstance) {
        for (TreePackage temp : packages)
            if (name.equals(temp.getName()))
                if (getInstance)
                    return temp;
                else 
                    return null;
        TreePackage tp = new TreePackage(name);
        packages.add(tp);
        tp.setId(idList.addId(tp));
        tp.setProjectId(id);
        calcParents();

        return tp;
    }
    
    public void  deletePackage(IdList idList, int packageId) {
        List<TreePackage> childPackages = new ArrayList<>();
        TreePackage tp = idList.getPackage(packageId);
     
        for (TreePackage temp : packages)
            if (temp.getParentId() == packageId)
                childPackages.add(temp);
        for (TreePackage temp : childPackages)
            deletePackage(idList, temp.getId());
        
        for (TreeFile temp : tp.getFiles())
            idList.removeId(temp.getId());
        
        packages.remove(tp);
        idList.removeId(packageId);
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
    
    @Override
    public int compareTo(TreeProject compareObject) {
        Collator collator = Collator.getInstance(new Locale("en", "US"));
        return collator.compare(name, compareObject.name);
    }
}