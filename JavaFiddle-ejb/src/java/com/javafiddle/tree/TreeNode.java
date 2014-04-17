package com.javafiddle.tree;

public interface TreeNode {
    public IdNodeType getNodeType();
    
    public long getId();

    public void setId(long id);
    
    public String getName();

    public void setName(String name);
    
    public String toJSON();
}
