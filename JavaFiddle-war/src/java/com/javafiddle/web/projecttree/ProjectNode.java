package com.javafiddle.web.projecttree;

import com.javafiddle.web.codemirror.FileEditions;
import java.io.File;

public class ProjectNode extends File{
    private ProjectNodeTypes type;
    private String parent;
    
    public ProjectNode(String path){
        super(path);
    }
    
    public static ProjectNode makeNode(String name, String parent, ProjectTree tree){
        String path = "." + File.separator + tree.getOwnerType() + File.separator
                + tree.getOwnerName() + File.separator 
                + ((parent == null)?(name):(parent + File.separator + name));
        ProjectNode node = new ProjectNode(path);
        node.setParent(parent);
        if(parent == null){
            node.setType(ProjectNodeTypes.ROOT);
        }
        else{
            if(name.endsWith(File.separator)){
                node.setType(ProjectNodeTypes.PACKAGE);
            }
            else{
                node.setType(ProjectNodeTypes.FILE);
            }
        }
        return node;
    }
    
    public void setType(ProjectNodeTypes type){
        this.type = type;
    }
    
    public void setParent(String parent){
        this.parent = parent;
    }
}
