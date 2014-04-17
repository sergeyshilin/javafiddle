package com.javafiddle.data.utils;

import com.javafiddle.tree.IdList;
import com.javafiddle.tree.Tree;
import java.util.TreeMap;

public class SessionData {
    Tree tree;
    IdList idList;
    TreeMap<Long, TreeMap<Long, String>> classes;

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public IdList getIdList() {
        return idList;
    }

    public void setIdList(IdList idList) {
        this.idList = idList;
    }

    public TreeMap<Long, TreeMap<Long, String>> getClasses() {
        return classes;
    }

    public void setClasses(TreeMap<Long, TreeMap<Long, String>> files) {
        this.classes = files;
    }
}
