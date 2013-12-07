package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.Project;
import com.javafiddle.core.jpa.Revision;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface ProjectManagerLocal {
    
    Project findById(long projectId);
    
    Project findByHashcode(String hashcode);
    
    Project createProject(Long userId, String hashcode, String name, String properties);
    
    List<Project> getUserProjects(long userId);
    
    Revision addTree(long projectId, Long parentId, String hashcode, Date date, String comment);
    
    List<Revision> getProjectTrees(long treeId);
    
    Revision findTreeById(long treeId);
    
    Revision findTreeByHashcode(String hashcode);
    
}
