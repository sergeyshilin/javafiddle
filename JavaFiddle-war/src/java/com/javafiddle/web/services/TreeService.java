package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.saving.SavingProjectRevision;
import com.javafiddle.web.services.utils.*;
import com.javafiddle.web.tree.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("")
@SessionScoped
public class TreeService implements Serializable {
    private static final Logger log = Logger.getLogger(TreeService.class.getName());
    FileHandler fh;

    Tree tree;
    IdList idList;
    ArrayList<String> packages;
    
    TreeMap<Date, Tree> projects = new TreeMap<>();
    TreeMap<Integer, TreeMap<Date, String>> files = new TreeMap<>();
        
    public TreeService() {
        idList = new IdList();
        tree = new Tree();
        packages = new ArrayList<>();
        
        try {
            fh = new FileHandler("C:/JavaFiddle/logging/TreeService.log"); 
            log.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        TreeFile tf = idList.getFile(id);
        if (tf == null)
            return Response.status(410).build();
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
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addPackage(
            @Context HttpServletResponse response,
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
    @Path("tree/classname")
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
    
    @GET
    @Path("tree/packagename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightPackage(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam("project_id") String id
            ) {
        Gson gson = new GsonBuilder().create();
        String result = "unknown";
        if(packages.isEmpty() && idList.isProject(TreeUtils.parseId(id))) {
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
    @Path("tree/rightprojectname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightProjectName(
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
    @Path("tree/rename")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response renameElement(
            @Context HttpServletResponse response,
            @FormParam("id") String idString,
            @FormParam("name") String name,
            @FormParam("type") String type
            ) {
        if (idString == null || name == null || type == null)
            return Response.status(400).build();
        int id = TreeUtils.parseId(idString);
        switch(type) {
            case "file":
                idList.getFile(id).setName(name + ".java");
                break;
            case "package":
                idList.getPackage(id).setName(name);
                break;
            case "root":
                idList.getProject(id).setName(name);
                break;
            default:
                break;
        }
        return Response.ok().build();
    }
       
    @POST
    @Path("tree/addFile")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addFile(
            @Context HttpServletResponse response,
            @FormParam("package_id") String idString,
            @FormParam("name") String name,
            @FormParam("type") String type
            ) {
        if (idString == null || name == null || type == null)
            return Response.status(400).build();
        Gson gson = new GsonBuilder().create();
        int id = TreeUtils.parseId(idString);
        TreePackage tp = idList.getPackage(id);
        TreeFile file = tp.addFile(idList, type, name + ".java");
        Revisions revisions = new Revisions(idList, files);
        revisions.addFileRevision(file, idList);
        return Response.ok(gson.toJson(file.getId()), MediaType.APPLICATION_JSON).build();
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
        TreeProject tpr = tree.getProjectInstance(idList, "MyFirstProject");
        TreePackage tp = tpr.getPackageInstance(idList, "com.myfirstproject.web");
        TreeFile main = tp.addFile(idList, "runnable", "Main.java");
        
        Revisions revisions = new Revisions(idList, files);
        revisions.addFileRevision(main, idList);
        
    }
    
    // ex ProjectRevisionsService
    //
    @POST
    @Path("revisions/project")
    public Response saveProjectRevision (
            @Context HttpServletRequest request
            ) throws InterruptedException {
        projects.put(new Date(), tree);
        SavingProjectRevision spr = new SavingProjectRevision(tree, idList, files);
        spr.SaveCurrent();
        Thread.sleep(10000);
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
        int addResult = revisions.addFileRevision(d);
        switch (addResult) {
            case 304: 
                log.log(Level.INFO, "{0}\tnot modified", d.getId());
                break;
            case 400:
                log.log(Level.INFO, "{0}\tbad request", d.getId());
                break;
            case 0:
                log.log(Level.INFO, "{0}\tsaved", d.getId());
                break;
            default:
                log.log(Level.INFO, "{0}\tresult: {1}", new Object[]{d.getId(), addResult});      
                break;
        }
        return Response.status(addResult == 304 ? 200 : addResult).build();
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