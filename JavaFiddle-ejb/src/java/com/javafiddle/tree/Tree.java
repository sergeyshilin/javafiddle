package com.javafiddle.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class Tree implements Serializable {
    private String repositoryId;
    private String repositoryName;
    
    private final List<TreeProject> projects = new ArrayList<>();
    
    public Tree() {
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public boolean isEmpty() {
        return projects.isEmpty();
    }

    public List<TreeProject> getProjects() {
        return projects;
    }
    
    public TreeProject addProject(IdList idList, String projectName) {
        for (TreeProject temp : projects)
            if (projectName.equals(temp.getName()))
                return temp;
        TreeProject tpr = new TreeProject(projectName);
        projects.add(tpr);
        tpr.setId(idList.add(tpr));
        tpr.addPackage(idList, "<default_package>");
        Collections.sort(projects);
        
        return tpr;
    }
    
   public TreeProject getProject(IdList idList, String projectName) {
        for (TreeProject temp : projects)
            if (projectName.equals(temp.getName()))
                return temp;
        return null;
    }
        
    public void deleteProject(IdList idList, int projectId) {
        TreeProject tpr = idList.getProject(projectId);
        for (TreePackage temp : tpr.getPackages()) {
            for (TreeClass tempFile : temp.getFiles())
                idList.remove(tempFile.getId());
            idList.remove(temp.getId());
        }
        
        projects.remove(tpr);
        idList.remove(projectId);
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");  
        sb.append("\"projects\"").append(":").append("[");
        for (TreeProject entry : projects)
            sb.append(entry.toJSON()).append(", ");
        if (!projects.isEmpty())
            sb.delete(sb.length()-2, sb.length());
        sb.append("]").append("}");
        return sb.toString();
    }
    
    public TreeMap<Long, TreeNode> getIdList() {
        TreeMap<Long, TreeNode> idList = new TreeMap<>();
        for (TreeProject entry : projects) {
            idList.put(entry.getId(), entry);
            idList.putAll(entry.getIdList());
        }
        return idList;
    }
}