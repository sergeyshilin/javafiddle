package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.Project;
import com.javafiddle.core.jpa.UserProfile;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProjectManager implements ProjectManagerLocal {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Project findById(long projectId) {
        return em.find(Project.class, projectId);
    }

    @Override
    public Project createProject(Long userId, String name, String properties) {
        UserProfile profile = em.find(UserProfile.class, userId);
        if (profile == null) {
            System.err.println("User not found: userId="+userId);
            return null;
        }
        Project project = new Project();
        project.setName(name);
        project.setProperties(properties);
        project.setUserProfile(profile);
        em.persist(project);
        
        return project;
    }

    @Override
    public List<Project> getUserProjects(long userId) {
        UserProfile profile = em.find(UserProfile.class, userId);
        if (profile == null) {
            System.err.println("User not found: userId="+userId);
            return null;
        }
        return em.createQuery("select p from Project p where p.userProfile = :profile", Project.class)
                .setParameter("profile", profile)
                .getResultList();
    }    
}
