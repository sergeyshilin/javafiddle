package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.pool.Task;
import com.javafiddle.pool.TaskPool;
import com.javafiddle.pool.TaskType;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.runner.Compilation;
import com.javafiddle.runner.Execution;
import com.javafiddle.runner.LaunchPermissions;
import com.javafiddle.saving.GetProjectRevision;
import com.javafiddle.saving.SavingProjectRevision;
import com.javafiddle.web.services.utils.*;
import com.javafiddle.web.tree.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("")
@SessionScoped
public class TreeService implements Serializable {
    Tree tree;
    IdList idList;
    ArrayList<String> packages;
    TaskPool pool;
    ArrayList<Date> projectRevisions;
    TreeMap<Integer, TreeMap<Date, String>> files;
        
    public TreeService() {
        resetData();
    }

    private void resetData() {
        idList = new IdList();
        tree = new Tree();
        packages = new ArrayList<>();
        pool = new TaskPool();
        projectRevisions = new ArrayList<>();
        files = new TreeMap<>();  
    }
    
    @GET
    @Path("tree")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request
            ) {
        if(tree.isEmpty())
            Utility.addExampleTree(tree, idList, files);
        return Response.ok(tree.toJSON(), MediaType.APPLICATION_JSON).build();
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
        int id = Utility.parseId(idString);
        TreeProject tpr = idList.getProject(id);
        tpr.addPackage(idList, name);
        packages.add(name);
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
        int id = Utility.parseId(idString);
        TreePackage tp = idList.getPackage(id);
        TreeFile file = tp.addFile(idList, type, name + ".java");
        Revisions revisions = new Revisions(idList, files);
        revisions.addFileRevision(file, idList);
        return Response.ok(gson.toJson(file.getId()), MediaType.APPLICATION_JSON).build();
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
    
    @GET
    @Path("tree/packagename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightPackageName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam("project_id") String id
            ) {
        Gson gson = new GsonBuilder().create();
        String result = "unknown";
        if(packages.isEmpty() && idList.isProject(Utility.parseId(id))) {
            packages.addAll(Tree.getPackagesNames(idList.getProject(Utility.parseId(id)).getPackages()));
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
        int id = Utility.parseId(idString);
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
    @Path("tree/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(
            @Context HttpServletRequest request,
            String idString
            ) {
        if (idString == null)
            return Response.status(400).build();
        int id = Utility.parseId(idString);
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
        int project_id = idList.getPackage(Utility.parseId(id)).getProjectId();
        String result = idList.getProject(project_id).getName();
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
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
        int id = Utility.parseId(idString);
        TreeFile tf = idList.getFile(id);
        if (tf == null)
            return Response.status(410).build();
        return Response.ok(gson.toJson(tf), MediaType.APPLICATION_JSON).build();
    }
    
    // ex ProjectRevisionsService
    //
    @POST
    @Path("revisions/project")
    public Response saveProjectRevision (
            @Context HttpServletRequest request
            ) {
        projectRevisions.add(new Date());
        SavingProjectRevision spr = new SavingProjectRevision(projectRevisions, tree, idList, files);			

        spr.saveProject();	

        Gson gson = new GsonBuilder().create();
        String hash = tree.hashes.getBranchHash() + tree.hashes.getTreeHash();
        return Response.ok(hash, MediaType.TEXT_PLAIN).build();
    }
    
    @GET
    @Path("revisions/project")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects(
            @Context HttpServletRequest request,
            @QueryParam("projecthash") String hash
            ) {
        if (hash == null)
            return Response.status(400).build();
        GetProjectRevision gpr = new GetProjectRevision(hash);
        if (!gpr.treeExists())
            return Response.status(404).build();
        resetData();
        tree = gpr.getTree();
        idList.putAll(tree.getIdList());
        ArrayList<TreeFile> filesList = new ArrayList<>();
        filesList.addAll(idList.getFileList().values());
        for (TreeFile tf : filesList) {
            int id = tf.getId();
            Date date = tf.getTimeStamp();
            String text = gpr.getFile(idList.getPackage(tf.getPackageId()).getName(), id, date);
            TreeMap<Date, String> revisions = new TreeMap<>();
            revisions.put(date, text);
            files.put(id, revisions);
        }
        
        return Response.ok(idList, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("revisions/project/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectList(
            @Context HttpServletRequest request
            ) {
        GetProjectRevision gpr = new GetProjectRevision(tree.hashes);
        ArrayList<Tree> trees = gpr.findParents(tree);
        if (trees == null)
           return Response.ok().build();
        ArrayList<String> names = new ArrayList<>();
        for (Tree entry : trees)
            names.add(entry.hashes.getTreeHash());
        return Response.ok(names, MediaType.APPLICATION_JSON).build();
    }
    
    // ex DocumentRevisionsSrevice
    //
    @POST
    @Path("revisions")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response saveFile(
            @Context HttpServletRequest request,
            @FormParam("id") String idString,
            @FormParam("timeStamp") String timeStamp,
            @FormParam("value") String value
            ) {
        if (idString == null || timeStamp == null || value == null)
            return Response.status(400).build();
        int id = Utility.parseId(idString);
        Revisions revisions = new Revisions(idList, files);
        int addResult = revisions.addFileRevision(id, timeStamp, value);
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
        int id = Utility.parseId(idString);
        
        Gson gson = new GsonBuilder().create();
        FileRevision fr;
        Date dateTime = idList.getFile(id).getTimeStamp();
        if (dateTime == null) {
            fr = new FileRevision("", "");
        } else {   
            String text = files.get(id).get(dateTime);
            fr = new FileRevision(Utility.DateToString(dateTime), text);
        }     
        return Response.ok(gson.toJson(fr), MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * Compile && Executing
     */
    
    @POST
    @Path("run/compile")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response compile(
            @Context HttpServletRequest request
            ) {
        if(true) {         
            AccessController.doPrivileged(new PrivilegedAction() {
                @Override
                public Object run() {
                    ArrayList<String> paths = null;
                    Task task = new Task(TaskType.COMPILATION, new Compilation(paths));
                    pool.add(task);
                    task.start();
    //                try {
    //                    task.sleep(15000);
    //                } catch (InterruptedException ex) {
    //                    Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
    //                }
    //                task.kill();

                    return null;
                }
            }, LaunchPermissions.getSecureContext());
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("run/execute")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response execute(
            @Context HttpServletRequest request
            ) {
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                String sep = File.separator;
                Task task = new Task(TaskType.EXECUTION, new Execution("-classpath " + "path to this pack", "com.myfirstproject.web.Main"));
                pool.add(task);
                task.start();
//                try {
//                    Thread.sleep(15000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                task.kill();

                return null;
            }
        }, LaunchPermissions.getSecureContext());
        return Response.ok().build();
    }

    @GET
    @Path("run/output")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutput(
            @Context HttpServletRequest request
            ){
            Gson gson = new GsonBuilder().create();
            
            Task task = pool.get(pool.size()-1);
            
            String result = task.getOutputStream();
            
//            if(!task.isCompleted()) {
//                long seconds = (task.getStartTime().getTime() - task.getEndTime().getTime()) / 1000;
//                if(seconds > 15) {
//                    task.kill();
//                    return Response.status(400).build();
//                }
//            }
            
            return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
     
    }
    
    @POST
    @Path("run/send")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setInput(
            @Context HttpServletRequest request,
            @FormParam("input") String input
            ) {
            
            Task task = pool.get(pool.size()-1);
            OutputStream stream = task.getInputStream();
//        try {
//            stream.write(new String(input + "\n").getBytes());
//        } catch (IOException ex) {
//            Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
//        }
            task.getProcess().send(input + "\n");
        return Response.ok().build();
    }
    
}