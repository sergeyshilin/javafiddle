package com.javafiddle.web.services.data;

import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.Tree;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class SessionData implements ISessionData {
    
    Tree tree;
    IdList idList;
    TreeMap<Integer, TreeMap<Long, String>> files;

    public SessionData() {
        reset();
    }

    private void reset() {
        idList = new IdList();
        tree = new Tree();
        files = new TreeMap<>();
    }
    
    @Override
    public void resetData() {
        reset();
    }
    
    @Override
    public Tree getTree() {
        return tree;
    }

    @Override
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    @Override
    public IdList getIdList() {
        return idList;
    }

    @Override
    public void setIdList(IdList idList) {
        this.idList = idList;
    }

    @Override
    public TreeMap<Integer, TreeMap<Long, String>> getFiles() {
        return files;
    }

    @Override
    public void setFiles(TreeMap<Integer, TreeMap<Long, String>> files) {
        this.files = files;
    }
}
