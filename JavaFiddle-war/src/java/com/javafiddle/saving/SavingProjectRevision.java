package com.javafiddle.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.IdNodeType;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.tree.TreeNode;
import com.javafiddle.web.tree.TreePackage;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class SavingProjectRevision implements Runnable {
    private static final String prefix = "C:\\JavaFiddle\\";
    Tree tree;
    IdList idList;
    TreeMap<Integer, TreeMap<Date, String>> files;
    
    public SavingProjectRevision(Tree tree, IdList idList, TreeMap<Integer, TreeMap<Date, String>> files) {
        this.tree = tree;
        this.idList = idList;
        this.files = files;
    }
    
    public void run() {
        SavingFile savingFile = new SavingFile("TestProject");
        
        savingFile.crearSrc();
                
        Gson gson = new GsonBuilder().create();
        savingFile.saveRevision("tree", new Date(), gson.toJson(tree));
        
        int id;
        TreeFile tf;
        Date time;
        TreePackage tp;
        for (Map.Entry<Integer, TreeNode> entry : idList.entrySet()){
            id = entry.getKey();
            if (entry.getValue().getNodeType() == IdNodeType.FILE) {
                tf = (TreeFile)entry.getValue();
                time = tf.getTimeStamp();
                savingFile.saveRevision(id, time, files.get(id).get(time));
                if (idList.get(tf.getPackageId()).getNodeType() == IdNodeType.PACKAGE) {
                    tp = (TreePackage)idList.get(tf.getPackageId());
                    savingFile.saveSrc(tf.getName(), tp.getName(), files.get(id).get(tf.getTimeStamp()));
                }
            }
        }
    }
}
