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
import java.math.BigInteger;
import java.nio.ByteBuffer;
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
            String hash = getHash(tree.getProjects().get(0).getName() + new Date().toString());
            if (hash == null)
                return;
            tree.setProjectHash(hash);
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
    
    private String getHash(String raw) {
        try {
            byte[] bytesOfMessage = raw.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byteHash = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1, byteHash);
            String numberHash = bigInt.toString().substring(0, 18);
            Long longHash = Long.parseLong(numberHash);
            StringBuilder charHash = new StringBuilder();
            for (int i = 0; i < 11; i++) {
                int chr = (int)(longHash%52);
                if (chr < 26) {
                    charHash.append((char)(chr + 'a'));
                } else {
                    charHash.append((char)(chr - 26 + 'A'));
                }
                longHash /= 52;
            }
            return charHash.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            return null;
        }
    }
}
