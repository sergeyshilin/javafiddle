package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.editions.FileEditions;
import com.javafiddle.web.services.utils.AddFileRevisionRequest;
import com.javafiddle.web.services.utils.FileRevision;
import com.javafiddle.web.services.utils.TreeUtils;
import com.javafiddle.web.tree.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("")
@SessionScoped
public class TreeService implements Serializable {
    Tree tree;
    IdList idList;
    Map<String, FileEditions> projects = new HashMap<>();
    Map<Integer, FileEditions> files = new HashMap<>();
        
    public TreeService() {
        idList = new IdList();
        tree = new Tree();
    }

    @GET
    @Path("tree")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request
            ) {
        if(tree.isEmpty())
            addExampleTree();
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(tree), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("tree/filedata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileData(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(400).build();
        Gson gson = new GsonBuilder().create();
        int id = TreeUtils.parseId(idString);
        
        return Response.ok(gson.toJson(idList.getFile(id)), MediaType.APPLICATION_JSON).build();
    }
     
    @POST
    @Path("tree/addProject")
    public Response addProject(
            @Context HttpServletRequest request,
            @QueryParam("name") String name
            ) {
        if (name == null)
            return Response.status(400).build();
        tree.addProject(idList, name);
        return Response.ok().build();
    }
    
    @POST
    @Path("tree/addPackage")
    public Response addPackage(
            @Context HttpServletRequest request,
            @QueryParam("projectId") String idString,
            @QueryParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        TreeProject tpr = idList.getProject(id);
        tpr.addPackage(idList, name);
        return Response.ok().build();
    }
       
    @POST
    @Path("tree/addFile")
    public Response addFile(
            @Context HttpServletRequest request,
            @QueryParam("packageId") String idString,
            @QueryParam("name") String name,
            @QueryParam("type") String type
            ) {
        if (idString == null || name == null || type == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        TreePackage tp = idList.getPackage(id);
        tp.addFile(idList, type, name);
        return Response.ok().build();
     }
    
    @POST
    @Path("tree/remove")
    public Response delete(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        if (!idList.isExist(id))
            return Response.status(400).build();
        switch (idList.getType(id)) {
            case PROJECT:
                tree.deleteProject(idList, id);
                break;
            case PACKAGE:
                TreePackage tp = idList.getPackage(id);
                TreeProject tpr = idList.getProject(tp.getProjectId());
                tpr.deletePackage(idList, id);
                break;
            case FILE:
                TreeFile tf = idList.getFile(id);
                TreePackage tpack = idList.getPackage(tf.getPackageId());
                tpack.deleteFile(idList, id);
                break;
            default:
                break;
        }
        return Response.ok().build();
    }
       
    private void addExampleTree() {
        TreeProject tpr = tree.getProjectInstance(idList, "NewProject");
        tpr.getPackageInstance(idList, "com.javafiddle.web.beans.death");
        TreePackage tp = tpr.getPackageInstance(idList, "com.javafiddle.web.projecttree.a.b.c.d.e.f.g.h.i");
        tp.addFile(idList, "class", "Reflections.java");
        tp = tpr.getPackageInstance(idList, "com.javafiddle.web.beans");
        tp.addFile(idList, "class", "CommonBean.java");
        tp.addFile(idList, "interface", "Example.txt");
        tp = tpr.getPackageInstance(idList, "com.javafiddle.web.codemirror"); 
        tp.addFile(idList, "class", "Dummy.java");
        tp.addFile(idList, "class", "FileEditions.java");
        tp = tpr.getPackageInstance(idList, "com.javafiddle.web.codemirror.gui.core");  
        tp.addFile(idList, "class", "ProjectEditions.java");
        tp = tpr.getPackageInstance(idList, "com.javafiddle.web.codemirror.gui.core.adding");  
        tp.addFile(idList, "class", "Tree.java");
        tpr.getPackageInstance(idList, "com.javafiddle.web.acore.appl");
        tpr.getPackageInstance(idList, "com.javafiddle.web.acore");
        tpr.getPackageInstance(idList, "com.javafiddle.web.acore.cpp");
        tpr.getPackageInstance(idList, "com.javafiddle.web.acore.cpp");
    }
    
    // ex ProjectRevisionsService
    //
    @GET
    @Path("revisions/project")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectRevisions(
            @Context HttpServletRequest request,
            @QueryParam("id") String id,
            @QueryParam("revision") String revision
            ) {
        
        return Response.ok("", MediaType.TEXT_PLAIN).build();
    }
    
    @POST
    @Path("revisions/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void saveProjectRevisions(
            @Context HttpServletRequest request,
            String data
            ) {
      
    }
    
    // ex DocumentRevisionsSrevice
    //
    @POST
    @Path("revisions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFileRevision(
            @Context HttpServletRequest request,
            String data
            ) {
        if (data == null)
            return Response.status(400).build();
        AddFileRevisionRequest d = new Gson().fromJson(data, AddFileRevisionRequest.class);
        if (d.getId() == null || d.getTimeStamp() == null || d.getValue() == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(d.getId());
        if (!idList.isFile(id))
            return Response.status(400).build();
        
        if (!files.containsKey(id))
            files.put(id, new FileEditions());
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date result = df.parse(d.getTimeStamp());
            files.get(id).addRevision(result, d.getValue());
            idList.getFile(id).setTimeStamp(result);
        } catch (ParseException ex) {
            return Response.status(400).build();
        }
        return Response.ok().build();
    }
    
    @GET
    @Path("revisions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileRevision(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        
        Gson gson = new GsonBuilder().create();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        FileRevision fr;
        Date dateTime = idList.getFile(id).getTimeStamp();
        if (dateTime == null) {
            fr = new FileRevision("", "");
        } else {    
            String text = files.get(id).getByTimeStamp(dateTime);
            fr = new FileRevision(df.format(dateTime), text);
        }     
        return Response.ok(gson.toJson(fr), MediaType.APPLICATION_JSON).build();
    }
}
