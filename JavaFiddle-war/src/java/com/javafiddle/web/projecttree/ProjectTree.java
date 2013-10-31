package com.javafiddle.web.projecttree;

import java.util.ArrayList;

public class ProjectTree extends ArrayList<ProjectNode> {
    private String project_name;
    private String owner_name;
    private OwnerType owner_type;
    
    public String getName(){
        return project_name;
    }
    public void setName(String name){
        project_name = name;
    }
    
    public OwnerType getOwnerType(){
        return owner_type;
    }
    
    public String getOwnerName(){
        return owner_name;
    }
}
