package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.Project;
import com.javafiddle.core.jpa.Revision;
import com.javafiddle.core.jpa.UserProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
    public Project findByHashcode(String hashcode) {
        try {
            return em.createQuery("select p from Project p where p.hashcode = :hashcode", Project.class)
                .setParameter("hashcode", hashcode)
                .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public Project createProject(Long userId, String hashcode, String name, String properties) {
        UserProfile profile = em.find(UserProfile.class, userId);
        if (profile == null) {
            System.err.println("User not found: userId="+userId);
            return null;
        }
        Project project = new Project();
        project.setName(name);
        project.setHashcode(hashcode);
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

    @Override
    public Revision addTree(long projectId, Long parentId, String hashcode, Date date, String comment) {
        Project project = findById(projectId);
        Revision parent = parentId == null ? null : findTreeById(parentId);
        Revision tree = new Revision();
        tree.setHashcode(hashcode);
        tree.setParent(parent);
        tree.setProject(project);
        tree.setCreationDate(date);
        tree.setComment(comment);
        em.persist(tree);
        
        return tree;
    }

    @Override
    public List<Revision> getProjectTrees(long treeId) {
        List<Revision> result = new ArrayList<>();
        Revision tree = em.find(Revision.class, treeId);
        while(tree != null) {
            result.add(tree);
            tree = tree.getParent();
        }
        Collections.reverse(result);
        return result;
    }

    @Override
    public Revision findTreeById(long treeId) {
        return em.find(Revision.class, treeId);
    }

    @Override
    public Revision findTreeByHashcode(String hashcode) {
        try {
            return em.createQuery("select t from Revision t where t.hashcode = :hashcode", Revision.class)
                .setParameter("hashcode", hashcode)
                .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
}
