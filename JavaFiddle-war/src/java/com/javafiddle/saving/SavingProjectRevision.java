package com.javafiddle.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.IdNodeType;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.tree.TreeNode;
import com.javafiddle.web.tree.TreePackage;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SavingProjectRevision implements Runnable {
    Tree tree;
    IdList idList;
    TreeMap<Integer, TreeMap<Date, String>> files;
    
    public SavingProjectRevision(Tree tree, IdList idList, TreeMap<Integer, TreeMap<Date, String>> files) {
        this.tree = tree;
        this.idList = idList;
        this.files = files;
    }
    
    @Override
    public void run() {
        if (tree.getProjectHash() == null) {
            try {
                String raw = tree.getProjects().get(0).getName() + new Date().toString();
                byte[] bytesOfMessage = raw.getBytes("UTF-8");

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(bytesOfMessage);
                tree.setProjectHash(bytesToHex(hash));
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                Logger.getLogger(SavingProjectRevision.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        
        SavingFile savingFile = new SavingFile(tree.getProjectHash());
        
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
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
