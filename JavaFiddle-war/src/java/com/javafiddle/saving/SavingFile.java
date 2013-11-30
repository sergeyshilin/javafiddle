package com.javafiddle.saving;

import com.javafiddle.web.services.TreeService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SavingFile {
    private static final String prefix = "C:\\JavaFiddle\\user\\guest\\";
    private static final Logger log = Logger.getLogger(SavingFile.class.getName());
    FileHandler fh;
    String projectId;
    
    public SavingFile(String projectId) {
        this.projectId = projectId;
        try {
            fh = new FileHandler("C:/JavaFiddle/logging/SavingFile.log"); 
            log.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void crearSrc() {
        deleteDirectory(new File(prefix + "src"));
    }
    
    public void saveRevision(int fileId, Date timeStamp, String text) {
        saveRevision(String.valueOf(fileId), timeStamp, text);
    }
     
    public void saveRevision(String fileId, Date timeStamp, String text) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String time = df.format(timeStamp);
        String path = prefix + "\\" + projectId + "\\revisions\\" + fileId + "\\" + time;
        writeFile(path, text);
    }
      
    public void saveCurrent(String fileName, String packageName, String text) {
        String path = prefix + "\\" + projectId + "\\src\\" + packageName.replace(".", "\\") + "\\" + fileName;
        writeFile(path, text); 
    }
    
    private void writeFile(String path, String text) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                } catch (NullPointerException e) {
                    log.log(Level.WARNING, "NullPointerException");
                }
                file.createNewFile();
            }
            try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
                writer.println(text);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "IOException");
        }
    }
    
public void deleteDirectory(File file)
  {
    if(!file.exists())
        return;
    if(file.isDirectory()) {
        for(File f : file.listFiles())
            deleteDirectory(f);
        file.delete();
        log.log(Level.INFO, "delete directory {0}", file.getName());
    } else {
      file.delete();
      log.log(Level.INFO, "delete file {0}", file.getName());
    }
  }
}
