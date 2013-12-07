package com.javafiddle.saving;

import com.javafiddle.web.services.utils.Utility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SavingFile {
    private static final String sep = File.separator;
    private static final String prefix = System.getProperty("user.home") + sep + "javafiddle_data";
    private static final String revisions = prefix + sep + "user" + sep + "guest";
    private static final String build = prefix + sep + "build";
    String projectId;
    
    public SavingFile(String projectId) {
        this.projectId = projectId;
    }
    
    public void saveTree(String hash, String text) {
        StringBuilder path = new StringBuilder();
        path.append(revisions).append(sep).append(projectId).append(sep).append("tree").append(sep).append(hash);
        writeFile(path.toString(), text); 
    }
    
    public void saveFileRevision(int fileId, Date timeStamp, String text) {
        StringBuilder path = new StringBuilder();
        path.append(revisions).append(sep).append(projectId).append(sep).append(fileId).append(sep).append(Utility.DateToString(timeStamp));
        writeFile(path.toString(), text);
    }
      
    public void saveSrcFile(String fileName, String packageName, String text) {
        StringBuilder path = new StringBuilder();
        path.append(build).append(sep).append(projectId).append(sep).append("src").append(sep).append(packageName.replace(".", sep)).append(sep).append(fileName);
        writeFile(path.toString(), text); 
    }
    
    private void writeFile(String path, String text) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(SavingFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println(text);
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(SavingFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void crearSrc() {
        StringBuilder path = new StringBuilder();
        path.append(build).append(sep).append(projectId).append(sep).append("src");
        deleteDirectory(new File(path.toString()));
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
