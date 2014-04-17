package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.Project;
import java.util.List;
import javax.ejb.Local;

@Local
public interface ProjectManagerLocal {
    
    Project findById(long projectId);
    
    Project createProject(Long userId, String name, String properties);
    
    List<Project> getUserProjects(long userId);
}
