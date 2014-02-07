package com.javafiddle.web.tree;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class TreeProject implements TreeNode, Comparable<TreeProject>, Serializable {
    private final IdNodeType nodeType = IdNodeType.PROJECT;
    private String name;
    private int id;
    private ArrayList<TreePackage> packages = new ArrayList<>();
    
    public TreeProject(String name) {
        this.name = name;
    }

    @Override
    public IdNodeType getNodeType() {
        return nodeType;
    }
        
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return name;
    }
        
    @Override
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TreePackage> getPackages() {
        return packages;
    }

    public TreePackage addPackage(IdList idList, String name) {
        for (TreePackage temp : packages)
            if (name.equals(temp.getName()))
                    return null;
        TreePackage tp = new TreePackage(name);
        packages.add(tp);
        tp.setId(idList.add(tp));
        tp.setProjectId(id);
        calcParents();

        return tp;
    }

    public TreePackage getPackage(String name) {
        for (TreePackage temp : packages)
            if (name.equals(temp.getName()))
                    return temp;
        return null;
    }
        
    public void deletePackage(IdList idList, int packageId) {
        List<TreePackage> childPackages = new ArrayList<>();
        TreePackage tp = idList.getPackage(packageId);
     
        for (TreePackage temp : packages)
            if (temp.getParentId() == packageId)
                childPackages.add(temp);
        for (TreePackage temp : childPackages)
            deletePackage(idList, temp.getId());
        
        for (TreeFile temp : tp.getFiles())
            idList.remove(temp.getId());
        
        packages.remove(tp);
        idList.remove(packageId);
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
                        packages.get(i).setShortName(packages.get(i).getName().substring(current.length() + 1));
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
    
    @Override
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\"").append(":").append(id).append(", ");
        sb.append("\"name\"").append(":\"").append(name).append("\", ");      
        sb.append("\"packages\"").append(":").append("[");
        for (TreePackage entry : packages)
            sb.append(entry.toJSON()).append(", ");
        if (!packages.isEmpty())
            sb.delete(sb.length()-2, sb.length());
        sb.append("]").append("}");
        return sb.toString();
    }
    
    public TreeMap<Integer, TreeNode> getIdList() {
        TreeMap<Integer, TreeNode> idList = new TreeMap<>();
        for (TreePackage entry : packages) {
            idList.put(entry.getId(), entry);
            idList.putAll(entry.getIdList());
        }
        return idList;
    }
}