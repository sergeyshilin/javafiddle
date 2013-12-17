package com.javafiddle.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.services.utils.Hashes;
import com.javafiddle.web.services.utils.Utility;
import com.javafiddle.web.tree.Tree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class GetProjectRevision {
    private static final String sep = File.separator;
    private static final String prefix = System.getProperty("user.home") + sep + "javafiddle_data" + sep + "user" + sep + "guest";

    private String branchHash;
    private String treeHash;

    public GetProjectRevision(String hash) {
        branchHash = hash.substring(0, Hashes.BRANCH_HASH_LENGTH);
        treeHash = hash.substring(Hashes.BRANCH_HASH_LENGTH, Hashes.BRANCH_HASH_LENGTH + Hashes.TREE_HASH_LENGTH);
    }

    public GetProjectRevision(Hashes hashes) {
        branchHash = hashes.getBranchHash();
        treeHash = hashes.getTreeHash();
    }
    
    public boolean treeExists() {
        String path = prefix + sep + branchHash + sep + "tree" + sep + treeHash;
        File file = new File(path);
        if (!file.exists())
            return false;
        return true;
    }
    
    public Tree getTree() {
         return getTree(treeHash);
    }
    
    public Tree getTree(String treeHash) {
        String path = prefix + sep + branchHash + sep + "tree" + sep + treeHash;
        String treejson = readFile(path);
        Gson gson = new GsonBuilder().create();

        return gson.fromJson(treejson, Tree.class);
    }
    
    public String getFile(String pack, int id, long date) {
        String path = prefix + sep + branchHash + sep + id + sep + date;
        String file = readFile(path);
        
        return file;
    }
    
    public ArrayList<Tree> findParents(Tree tree) {
        ArrayList<Tree> treeList = new ArrayList<>();
        Tree current = tree;
        if (current == null)
            return null;
        treeList.add(current);
        int i = 0;
        while (current.hashes.getParentTreeHash() != null && i < 50) {
            current = getTree(current.hashes.getParentTreeHash());
            if (current == null)
                break;
            treeList.add(current); 
            i++;
        }

        return treeList;
    } 
    
    public static String readFile(String path) {
        StringBuilder text = new StringBuilder();
        try {
            try (BufferedReader reader = new BufferedReader(
		   new InputStreamReader(
                      new FileInputStream(path), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null)
                    text.append(line).append("\n");
            }
        } catch(IOException e) {
            System.out.println("I/O Exception while reading file");
        }

        return text.toString();
    }
}
