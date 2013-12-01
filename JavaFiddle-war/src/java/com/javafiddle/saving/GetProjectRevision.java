package com.javafiddle.saving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GetProjectRevision {
    private static final String sep = File.separator;
    private static final String prefix = System.getProperty("user.home") + sep + "user" + sep + "guest";
    
    public String readFile(String hash) {
        File file = new File(prefix + sep + hash);
        if (!file.exists())
            return "not exists, sorry";
        return viewAll(file);
    }

    public String viewAll(File file) {
        StringBuilder sb = new StringBuilder();
        
        if(!file.exists())
            return "";
        if(file.isDirectory()) {
            sb.append(file.getName()).append("\n");
            for(File f : file.listFiles())
                sb.append(viewAll(f)).append("\n");
        } else {
          sb.append(file.getName());
        }
        return sb.toString().replace("\n", "\n ");
        
    }
}
