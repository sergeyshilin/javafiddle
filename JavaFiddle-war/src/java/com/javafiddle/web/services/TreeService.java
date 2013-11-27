package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.web.services.utils.AddFileRevisionRequest;
import com.javafiddle.web.services.utils.FileRevision;
import com.javafiddle.web.services.utils.SaveAllFilesRequest;
import com.javafiddle.web.services.utils.TreeUtils;
import com.javafiddle.web.tree.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("")
@SessionScoped
public class TreeService implements Serializable {
    Tree tree;
    IdList idList;
    ArrayList<String> packages;
    
    TreeMap<Integer, TreeMap<Date, TreeProject>> projects = new TreeMap<>();
    TreeMap<Integer, TreeMap<Date, String>> files = new TreeMap<>();
        
    public TreeService() {
        idList = new IdList();
        tree = new Tree();
        packages = new ArrayList<>();
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
    @Consumes({"application/x-www-form-urlencoded"})
    public Response addProject(
            @Context HttpServletRequest request,
            @FormParam("name") String name
            ) {
        if (name == null)
            return Response.status(400).build();
        tree.addProject(idList, name);
        return Response.ok().build();
    }
    
    @POST
    @Path("tree/addPackage")
    @Consumes({"application/x-www-form-urlencoded"})
    public Response addPackage(
            @Context HttpServletRequest request,
            @FormParam("project_id") String idString,
            @FormParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        TreeProject tpr = idList.getProject(id);
        tpr.addPackage(idList, name);
        packages.add(name);
        return Response.ok().build();
    }
    
    @GET
    @Path("tree/classfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightClassName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name
            ) {
        Gson gson = new GsonBuilder().create();
        boolean result = false;
        if(name.matches("([a-zA-Z][a-zA-Z0-9_]*)")){
            result = !result;
        }
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
       
    @POST
    @Path("tree/addFile")
    @Consumes({"application/x-www-form-urlencoded"})
    public Response addFile(
            @Context HttpServletRequest request,
            @FormParam("package_id") String idString,
            @FormParam("name") String name,
            @FormParam("type") String type
            ) {
        if (idString == null || name == null || type == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        TreePackage tp = idList.getPackage(id);
        tp.addFile(idList, type, name + ".java");
        return Response.ok().build();
     }
    
    @POST
    @Path("tree/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(
            @Context HttpServletRequest request,
            String idString
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
    
    @GET
    @Path("tree/package")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightPackage(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam("project_id") String id
            ) {
        Gson gson = new GsonBuilder().create();
        String result = "unknown";
        if(packages.isEmpty()) {
            packages.addAll(Tree.getPackagesNames(idList.getProject(TreeUtils.parseId(id)).getPackages()));
        }
        
        if(!name.matches("(([a-zA-Z][a-zA-Z0-9]*)(\\.)?)+")) {
            result = "wrongname";
        } else if(packages.contains(name)) {
            result = "used";
        } else {
            result = "ok";
        }
        
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("tree/projectname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectName(
            @Context HttpServletRequest request,
            @QueryParam("id") String id
            ) {
        Gson gson = new GsonBuilder().create();
        int project_id = idList.getPackage(TreeUtils.parseId(id)).getProjectId();
        String result = idList.getProject(project_id).getName();
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
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
    @POST
    @Path("revisions/project")
    public Response saveProjectRevision (
            @Context HttpServletRequest request,
            @QueryParam("id") String idString,
            @QueryParam("timeStamp") String timeStamp     
            ) {
        if (idString == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        if (!idList.isProject(id))
            return Response.status(400).build();
        
        TreeProject tp = idList.getProject(id);
        if (!projects.containsKey(id))
            projects.put(id, new TreeMap<Date, TreeProject>());
        else {
            if (tp.equals(projects.lastEntry().getValue()))
                return Response.status(304).build();
        }
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date result = df.parse(timeStamp);
            projects.get(id).put(result, tp);
        } catch (ParseException ex) {
            return Response.status(400).build();
        }
        return Response.ok().build();
    }
    
    @GET
    @Path("revisions/project")
    public Response getProjectRevisions(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString,
            @QueryParam("timeStamp") String timeStamp
            ) {
        if (idString == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        if (idList.isProject(id)) {
            // to do
        }    
        return Response.ok("", MediaType.TEXT_PLAIN).build();
    }
    
    // ex DocumentRevisionsSrevice
    //
    @POST
    @Path("revisions/saveAllFiles")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveAllFiles(
            @Context HttpServletRequest request,
            String data
            ) {
        if (data == null)
            return Response.status(400).build();
        SaveAllFilesRequest d = new Gson().fromJson(data, SaveAllFilesRequest.class);
        Revisions revisions = new Revisions(idList, files);
        switch (revisions.saveAllFiles(d.getFiles())) {
            case 0: return Response.ok().build();
            case 400: return Response.status(400).build();
            default: return Response.status(500).build();
        }
    }
        
    @POST
    @Path("revisions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveFile(
            @Context HttpServletRequest request,
            String data
            ) {
        if (data == null)
            return Response.status(400).build();
        AddFileRevisionRequest d = new Gson().fromJson(data, AddFileRevisionRequest.class);
        Revisions revisions = new Revisions(idList, files);
        switch (revisions.addFileRevision(d)) {
            case 400: return Response.status(400).build();
            default: return Response.ok().build();
        }
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
        FileRevision fr;
        Date dateTime = idList.getFile(id).getTimeStamp();
        if (dateTime == null) {
            fr = new FileRevision("", "");
        } else {   
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String text = files.get(id).get(dateTime);
            fr = new FileRevision(df.format(dateTime), text);
        }     
        return Response.ok(gson.toJson(fr), MediaType.APPLICATION_JSON).build();
    }
}