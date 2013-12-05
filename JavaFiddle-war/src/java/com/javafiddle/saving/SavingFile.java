package com.javafiddle.saving;

import com.javafiddle.web.services.utils.Utility;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class SavingFile {
    private static final String sep = File.separator;
    private static final String prefix = System.getProperty("user.home") + sep + "user" + sep + "guest";
    String projectId;
    
    public SavingFile(String projectId) {
        this.projectId = projectId;
    }
    
    public void saveRevisionsList(ArrayList<Date> dateList) {
        String path = prefix + sep + projectId + sep + "revisions" + sep + "list";
        StringBuilder sb = new StringBuilder();
        for (Date temp : dateList)
            sb.append(Utility.DateToString(temp)).append(", ");
        if (!dateList.isEmpty())
            sb.delete(sb.length()-2, sb.length());
        writeFile(path, sb.toString());
    }
    
    public void saveTree(String hash, String text) {
        String path = prefix + sep + projectId + sep + "revisions" + sep + "tree" + sep + hash;
        writeFile(path, text); 
    }
    
    public void saveRevision(int fileId, Date timeStamp, String text) {
        String path = prefix + sep + projectId + sep + "revisions" + sep + fileId + sep + Utility.DateToString(timeStamp);
        writeFile(path, text);
    }
      
    public void saveSrc(String fileName, String packageName, String text) {
        String path = prefix + sep + projectId + sep + "src" + sep + packageName.replace(".", sep) + sep + fileName;
        writeFile(path, text); 
    }
    
    private void writeFile(String path, String text) {
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                try {
                    file.getParentFile().mkdirs();
                } catch (NullPointerException e) {
                    
                }
                file.createNewFile();
            }
            try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
                writer.println(text);
            }
        } catch (IOException e) {
            
        }
    }
    
    public void crearSrc() {
        deleteDirectory(new File(prefix + sep + projectId + sep + "src"));
    }
    
    public void deleteDirectory(File file) {
        if(!file.exists())
            return;
        if(file.isDirectory()) {
            for(File f : file.listFiles())
                deleteDirectory(f);
            file.delete();
        } else {
          file.delete();
        }
    }
}
