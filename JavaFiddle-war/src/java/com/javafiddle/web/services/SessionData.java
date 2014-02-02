package com.javafiddle.web.services;

import com.javafiddle.core.jpa.Project;
import com.javafiddle.pool.TaskPool;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.Tree;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class SessionData implements ISessionData {
    static final String SEP = File.separator;
    static final String PREFIX = System.getProperty("user.home") + SEP + "javafiddle_data";
    static final String BUILD = PREFIX + SEP + "build";
    
    Tree tree;
    IdList idList;
    TaskPool pool;
    ArrayList<Long> projectRevisions;
    TreeMap<Integer, TreeMap<Long, String>> files;
    Project project;

    public SessionData() {
        reset();
    }

    private void reset() {
        idList = new IdList();
        tree = new Tree();
        pool = new TaskPool();
        projectRevisions = new ArrayList<>();
        files = new TreeMap<>();
        project = null;
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
    public TaskPool getPool() {
        return pool;
    }

    @Override
    public void setPool(TaskPool pool) {
        this.pool = pool;
    }

    @Override
    public ArrayList<Long> getProjectRevisions() {
        return projectRevisions;
    }

    @Override
    public void setProjectRevisions(ArrayList<Long> projectRevisions) {
        this.projectRevisions = projectRevisions;
    }

    @Override
    public TreeMap<Integer, TreeMap<Long, String>> getFiles() {
        return files;
    }

    @Override
    public void setFiles(TreeMap<Integer, TreeMap<Long, String>> files) {
        this.files = files;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }
}
