package com.javafiddle.web.tree;

import com.javafiddle.core.ejb.util.IdGeneratorLocal;
import java.io.Serializable;
import java.util.HashMap;
import javax.naming.InitialContext;

public class IdList extends HashMap<Integer, TreeNode> implements Serializable {
    private int count = 0;
    private IdGeneratorLocal idGenerator = null;
        
    public IdList() {
        try {
            idGenerator = (IdGeneratorLocal) new InitialContext().lookup("java:global/JavaFiddle/JavaFiddle-ejb/IdGenerator!com.javafiddle.core.ejb.util.IdGeneratorLocal");
            count = (int) idGenerator.getNextId();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected int add(TreeNode treeNode) {
        int id = count;
        super.put(id, treeNode);
        count = (int) idGenerator.getNextId();
        System.out.println("id = " + count);
        return id;  
    }
    
    public boolean isExist(int id) {
        if (get(id) == null)
            return false;
        return true;   
    }
    
    public IdNodeType getType(int id) {
        return get(id).getNodeType();
    }
    
    public boolean isProject(int id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.PROJECT;
        return false;
    } 
    
    public boolean isPackage(int id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.PACKAGE;
        return false;
    } 
    
    public boolean isFile(int id) {
        if (containsKey(id))
            return getType(id) == IdNodeType.FILE;
        return false;
    } 
    
    public TreeProject getProject(int id) {
        if (isProject(id))
            return (TreeProject)get(id);
        return null;
    }
    
    public TreePackage getPackage(int id) {
      //  System.out.println(get(id).getNodeType());
     //   if (isPackage(id))
            return (TreePackage)get(id);
     //   return null;
    }
    
    public TreeFile getFile(int id) {
        if (isFile(id))
            return (TreeFile)get(id);
        return null;
    } 
    
    public HashMap<Integer, TreeFile> getFileList() {
        HashMap<Integer, TreeFile> fileList = new HashMap<>();
        for (int id : this.keySet())
            if (isFile(id))
                fileList.put(id, getFile(id));
        return fileList;
    }
}

