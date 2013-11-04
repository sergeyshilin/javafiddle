package com.javafiddle.web.codemirror;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private List<TreeProject> projects = new ArrayList<>();
            
    public void addFile(String project, String pack, String type, String title) {
        TreeProject tpr = getProject(project);
        TreePackage tp = tpr.getPackage(pack);
        tp.getFile(type, title);
    }
    
   public void addPackage(String project, String pack) {
        TreeProject tpr = getProject(project);
        tpr.getPackage(pack);
    }
    
    private TreeProject getProject(String project) {
        TreeProject tp = null;
        for (TreeProject temp : projects)
            if (project.equals(temp.getProject()))
                tp = temp;
        if (tp == null) {
            tp = new TreeProject(project);
            projects.add(tp);
        }
        return tp;
    }
}


class TreeProject {
    private String project;
    private List<TreePackage> packages = new ArrayList<>();
    
    public TreeProject() {
    }

    public TreeProject(String project) {
        this.project = project;
    }
    
    public String getProject() {
        return project;
    }
        
    public TreePackage getPackage(String pack) {
        TreePackage tp = null;
        for (TreePackage temp : packages)
            if (pack.equals(temp.getPack()))
                tp = temp;
        if (tp == null) {
            tp = new TreePackage(pack);
            packages.add(tp);
            calcParents();
        }
        
        return tp;
    }
    
    private void calcParents() {
        int count = 0;
        for (TreePackage temp : packages)
            count++;
        
        int[] level = new int[count];
        int[] parent = new int[count];
        for (int i = 0; i < count; i++) {
            String current = packages.get(i).getPack();
            level[i] = current.replaceAll("[^\\.]", "").length();
            
            parent[i] = -1;
            String nearparent = current;
            while (parent[i] == -1 && !nearparent.equals("")) {
                nearparent = current.substring(0, nearparent.lastIndexOf('.') == -1 ? 0 : nearparent.lastIndexOf('.'));
                for (int j = 0; j < count; j++)
                    if (i != j && nearparent.equals(packages.get(j).getPack()))
                        parent[i] = j;
            }
        }
        
        int minlevel = level[0];
        int maxlevel = level[0];
        for (int i = 0; i < count; i++) {
            if (level[i] > maxlevel)
                maxlevel = level[i];
            if (level[i] < minlevel)
                minlevel = level[i];            
        }
        
        for (int i = minlevel + 1; i <= maxlevel; i++)
            for (int j = 0; j < count; j++)
                if (parent[j] != -1 && level[j] == i)
                    packages.get(j).setParent(packages.get(parent[j]).getParent() + "_" + packages.get(parent[j]).getPack());
    }
}


class TreePackage {
    private String pack;
    private String parent;

    private List<SourceFile> files = new ArrayList<>();
    
    public TreePackage() {
    }
        
    public TreePackage(String pack) {
        this.pack = pack;
        this.parent = "";
    }

    public String getPack() {
        return pack;
    }
        
     public SourceFile getFile(String type, String title) {
        SourceFile sf = null;
        for (SourceFile temp : files)
            if (title.equals(temp.getName()))
                sf = temp;
        if (sf == null) {
            sf = new SourceFile(title, type);
            files.add(sf);
        }
        return sf;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public String getParent() {
        return parent;
    }

}
