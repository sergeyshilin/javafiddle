package com.javafiddle.saving;

import com.javafiddle.web.services.TreeService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavingFile {
    private static final String sep = File.separator;
    private static final String prefix = "C:" + sep + "user" + sep + "guest";
    String projectId;
    
    public SavingFile(String projectId) {
        this.projectId = projectId;
    }
    
    public void saveRevision(int fileId, Date timeStamp, String text) {
        saveRevision(String.valueOf(fileId), timeStamp, text);
    }
     
    public void saveRevision(String fileId, Date timeStamp, String text) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String time = df.format(timeStamp);
        String path = prefix + sep + projectId + sep + "revisions" + sep + fileId + sep + time;
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
