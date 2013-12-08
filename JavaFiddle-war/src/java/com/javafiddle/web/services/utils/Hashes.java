package com.javafiddle.web.services.utils;

public class Hashes {
    public final static int BRANCH_HASH_LENGTH = 7;
    public final static int TREE_HASH_LENGTH = 5;
    
    private String branchHash;
    private String treeHash;
    private String parentTreeHash;

    public String getBranchHash() {
        return branchHash;
    }

    public void setBranchHash(String branchHash) {
        this.branchHash = branchHash;
    }

    public String getTreeHash() {
        return treeHash;
    }

    public void setTreeHash(String treeHash) {
        this.treeHash = treeHash;
    }

    public String getParentTreeHash() {
        return parentTreeHash;
    }

    public void setParentTreeHash(String parentTreeHash) {
        this.parentTreeHash = parentTreeHash;
    }
}
