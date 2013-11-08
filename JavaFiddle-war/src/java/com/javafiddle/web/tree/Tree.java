package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class Tree implements Serializable {
    private static Tree  instance;
    private List<TreeProject> projects = new ArrayList<>();
    
    private Tree  ()
    {}
 
    public static Tree getInstance() {
        if (instance == null)
            instance = new Tree();
        return instance;
    }
    
    public void add(String projectName) {
        getProject(projectName);
    }
    
    public void add(String projectName, String packageName) {
        TreeProject tpr = getProject(projectName);
        tpr.getPackage(packageName);
    }
            
    public void add(String projectName, String packageName, String type, String fileName, String timeStamp) {
        TreeProject tpr = getProject(projectName);
        TreePackage tp = tpr.getPackage(packageName);
        tp.addFile(type, fileName);
    }
    
    public void deleteFileById(int id) {
        TreeFile tf = (TreeFile)IdList.getInstance().getById(id);
        TreePackage tp = (TreePackage)IdList.getInstance().getById(tf.getPackageId());
        tp.deleteFile(tf);
        IdList.getInstance().removeId(id);
    }
    
    private TreeProject getProject(String projectName) {
        TreeProject tpr = null;
        for (TreeProject temp : projects)
            if (projectName.equals(temp.getName()))
                tpr = temp;
        if (tpr == null) {
            tpr = new TreeProject(projectName);
            projects.add(tpr);
            tpr.setId(IdList.getInstance().addId(tpr));
        }
        return tpr;
    }
}
