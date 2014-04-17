package com.javafiddle.data;

import com.javafiddle.core.ejb.ProjectManagerLocal;
import com.javafiddle.data.utils.FileSaver;
import com.javafiddle.data.utils.SessionData;
import com.javafiddle.data.utils.gitExecutor;
import com.javafiddle.tree.IdList;
import com.javafiddle.tree.Tree;
import com.javafiddle.tree.TreeClass;
import com.javafiddle.tree.TreePackage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.ejb.EJB;

/**
 *
 * @author wawilon
 */
public class ProjectManager {
    
    @EJB
    private ProjectManagerLocal pm;
    
    public String saveProject(SessionData sd) {
        Tree tree = sd.getTree();
        IdList idList = sd.getIdList();
        TreeMap<Long, TreeMap<Long, String>> classes = sd.getClasses();
        
        String repositoryId = tree.getRepositoryId();
        if (repositoryId == null) {
            pm.createProject(0L, tree.getRepositoryName(), null);
            tree.setRepositoryId(gitExecutor.createRepository());
        }
        
        FileSaver savingFile = new FileSaver(repositoryId);
        
        savingFile.crearSrc();
               
        ArrayList<TreeClass> filesList = new ArrayList<>();
        filesList.addAll(sd.getIdList().getFileList().values());
        for (TreeClass tf : filesList) {
            long id = tf.getId();
            TreePackage tp;
            if ((tp = idList.getPackage(tf.getPackageId())) != null)
                savingFile.saveSrcFile(tf.getName(), tp.getName(), classes.get(id).get(tf.getTimeStamp()));
        }
        
        return gitExecutor.commit();
    }

    public SessionData loadProject(String hash) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
