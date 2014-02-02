package com.javafiddle.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.services.utils.Hashes;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.tree.TreePackage;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectRevisionSaver {
    Tree tree;
    IdList idList;
    TreeMap<Integer, TreeMap<Long, String>> files;
    
    public ProjectRevisionSaver(Tree tree, IdList idList, TreeMap<Integer, TreeMap<Long, String>> files) {
        this.tree = tree;
        this.idList = idList;
        this.files = files;
    }
    
    public void saveRevision() {
        if ("".equals(tree.getHashes().getBranchHash())) {
            try {
                StringBuilder rawHash = new StringBuilder();
                rawHash.append(tree.getProjects().get(0).getName()).append(new Date().getTime());
                String hash = getHash(rawHash.toString(), Hashes.BRANCH_HASH_LENGTH);
                tree.getHashes().setBranchHash(hash);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                Logger.getLogger(ProjectRevisionSaver.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        
        SavingFile savingFile = new SavingFile(tree.getHashes().getBranchHash());
        
        // saving tree
        try {
            Gson gson = new GsonBuilder().create();
            tree.getHashes().setParentTreeHash(tree.getHashes().getTreeHash());
            StringBuilder rawHash = new StringBuilder();
            rawHash.append(new Date().toString()).append(new Date().getTime());
            tree.getHashes().setTreeHash(getHash(rawHash.toString(), Hashes.TREE_HASH_LENGTH));
            savingFile.saveTree(tree.getHashes().getTreeHash(), gson.toJson(tree));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(ProjectRevisionSaver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        // saving files
        ArrayList<TreeFile> filesList = new ArrayList<>();
        filesList.addAll(idList.getFileList().values());
        for (TreeFile tf : filesList) {
            int id = tf.getId();
            long time = tf.getTimeStamp();
            savingFile.saveFileRevision(id, time, files.get(id).get(time));
        }
    }
    
    public String saveSrc(String srcHash) {
        if (srcHash.equals("")) { 
            try {
                StringBuilder rawHash = new StringBuilder();
                rawHash.append(tree.getProjects().get(0).getName()).append(new Date().toString()).append(System.currentTimeMillis());
                String hash = getHash(rawHash.toString(), Hashes.BRANCH_HASH_LENGTH);
                srcHash = hash;
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                Logger.getLogger(ProjectRevisionSaver.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        
        SavingFile savingFile = new SavingFile(srcHash);
        
        savingFile.crearSrc();
               
         // saving files
        ArrayList<TreeFile> filesList = new ArrayList<>();
        filesList.addAll(idList.getFileList().values());
        for (TreeFile tf : filesList) {
            int id = tf.getId();
            TreePackage tp;
            if ((tp = idList.getPackage(tf.getPackageId())) != null)
                savingFile.saveSrcFile(tf.getName(), tp.getName(), files.get(id).get(tf.getTimeStamp()));
        }
        
        return srcHash;
    }
        
    private static String getHash(String raw, int length) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = raw.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] byteHash = md.digest(bytesOfMessage);
        ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(byteHash, 0, 8));
        long longHash = Math.abs(bb.getLong());
        StringBuilder charHash = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int chr = (int)(longHash%52);
            if (chr < 26) {
                charHash.append((char)(chr + 'a'));
            } else {
                charHash.append((char)(chr - 26 + 'A'));
            }
            longHash /= 52;
        }
        return charHash.toString();
    }
}
