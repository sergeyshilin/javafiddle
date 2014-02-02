package com.javafiddle.web.services;

import com.javafiddle.core.jpa.Project;
import com.javafiddle.pool.TaskPool;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.Tree;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

public interface ISessionData extends Serializable {
       
    public void resetData();
     
    public Tree getTree();

    public void setTree(Tree tree);

    public IdList getIdList();

    public void setIdList(IdList idList);

    public TaskPool getPool();

    public void setPool(TaskPool pool);

    public ArrayList<Long> getProjectRevisions();

    public void setProjectRevisions(ArrayList<Long> projectRevisions);

    public TreeMap<Integer, TreeMap<Long, String>> getFiles();

    public void setFiles(TreeMap<Integer, TreeMap<Long, String>> files);

    public Project getProject();

    public void setProject(Project project);
    
    
}
