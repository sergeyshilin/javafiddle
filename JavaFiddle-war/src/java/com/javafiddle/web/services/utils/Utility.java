package com.javafiddle.web.services.utils;

import com.javafiddle.revisions.Revisions;
import com.javafiddle.web.tree.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utility {
    public static int parseId(String idString) {
        if(isInteger(idString))
            return Integer.parseInt(idString);
        if (idString.startsWith("node_")) {
            idString = idString.substring("node_".length());
            if (idString.endsWith("_tab"))
                idString = idString.substring(0, idString.length() - "_tab".length());
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

    public static void addExampleTree(Tree tree, IdList idList, TreeMap<Integer, TreeMap<Date, String>> files) {
        TreeProject tpr = tree.getProjectInstance(idList, "MyFirstProject");
        TreePackage tp = tpr.getPackageInstance(idList, "com.myfirstproject.web");
        TreeFile main = tp.addFile(idList, "runnable", "Main.java");
        
        Revisions revisions = new Revisions(idList, files);
        revisions.addFileRevision(main, idList);
    }
    
    public static String DateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return df.format(date);
    }
    
    public static Date StringToDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            return df.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
