package com.javafiddle.web.tree;

public interface TreeNode {
    public IdNodeType getNodeType();
    
    public int getId();

    public void setId(int id);
    
    public String getName();

    public void setName(String name);
    
    public String toJSON();
}
