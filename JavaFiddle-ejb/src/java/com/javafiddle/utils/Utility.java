package com.javafiddle.utils;

import com.javafiddle.tree.IdList;
import com.javafiddle.tree.TreeClass;
import com.javafiddle.tree.TreePackage;
import com.javafiddle.tree.Tree;
import com.javafiddle.tree.TreeProject;
import com.javafiddle.web.services.data.FileRevisions;
import java.util.TreeMap;

public class Utility {
    public static int parseId(String idString) {
        if(isInteger(idString))
            return Integer.parseInt(idString);
        if (idString.startsWith("node_")) {
            idString = idString.substring("node_".length());
            if (idString.endsWith("_tab"))
                idString = idString.substring(0, idString.length() - "_tab".length());
            if (idString.endsWith("_srcfolder"))
                idString = idString.substring(0, idString.length() - "_srcfolder".length());
            return Integer.parseInt(idString);
        }
        return -1;
    }
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        
        return true;
    }

    public static void addExampleTree(Tree tree, IdList idList, TreeMap<Integer, TreeMap<Long, String>> files) {
        TreeProject tpr = tree.addProject(idList, "MyFirstProject");
        TreePackage tp = tpr.addPackage(idList, "com.myfirstproject.web");
        TreeClass main = tp.addFile(idList, "runnable", "Main.java");
        
        FileRevisions revisions = new FileRevisions(idList, files);
        revisions.addFileRevision(main, idList);
    }
}
