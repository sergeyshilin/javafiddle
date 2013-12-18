package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.core.ejb.ProjectManagerLocal;
import com.javafiddle.core.ejb.UserManagerLocal;
import com.javafiddle.core.ejb.util.IdGeneratorLocal;
import com.javafiddle.core.jpa.Project;
import com.javafiddle.core.jpa.Revision;
import com.javafiddle.pool.*;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.runner.*;
import com.javafiddle.saving.GetProjectRevision;
import com.javafiddle.saving.ProjectRevisionSaver;
import com.javafiddle.web.services.utils.*;
import com.javafiddle.web.tree.*;
import com.javafiddle.web.utils.SessionUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("")
@SessionScoped
public class TreeService implements Serializable {
    private static final String sep = File.separator;
    private static final String prefix = System.getProperty("user.home") + sep + "javafiddle_data";
    private static final String build = prefix + sep + "build";
    
    Tree tree;
    IdList idList;
    ArrayList<String> packages;
    TaskPool pool;
    ArrayList<Long> projectRevisions;
    TreeMap<Integer, TreeMap<Long, String>> files;
    String srcHash;
    Project project;
    
    @Inject
    private UserManagerLocal um;
    @Inject
    private IdGeneratorLocal idGenerator;
    @Inject
    private ProjectManagerLocal pm;
    
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
        project = null;
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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addPackage(
            @Context HttpServletResponse response,
            @FormParam("project_id") String idString,
            @FormParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        TreeProject tpr = idList.getProject(id);
        tpr.addPackage(idList, name);
        packages.addAll(Tree.getAllPossiblePackages(name));
        
        return Response.ok().build();
    }
     
    @POST
    @Path("tree/addFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFile(
            @Context HttpServletResponse response,
            @FormParam("package_id") String idString,
            @FormParam("name") String name,
            @FormParam("type") String type
            ) {
        if (idString == null || name == null || type == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        TreePackage tp;
        if (idList.isProject(id))
            tp = idList.getProject(id).getPackageInstance(idList, "!default_package");
        else 
            tp = idList.getPackage(id);
        TreeFile file = tp.addFile(idList, type, name + ".java");
        Revisions revisions = new Revisions(idList, files);
        revisions.addFileRevision(file, idList);
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(file.getId()), MediaType.APPLICATION_JSON).build();
     }
        
    @GET
    @Path("tree/rightprojectname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightProjectName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name
            ) {
        if (name == null)
            return Response.status(401).build();
        
        boolean result = false;
        if(name.matches("([a-zA-Z][a-zA-Z0-9_]*)"))
            result = !result;
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("tree/projectname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectName(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        int project_id;
        if (idList.isProject(id))
            project_id = id;
        else
            project_id = idList.getPackage(id).getProjectId();
        String result = idList.getProject(project_id).getName();
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
        
    @GET
    @Path("tree/packagename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightPackageName(
            @Context HttpServletRequest request,
            @QueryParam("project_id") String idString,
            @QueryParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(401).build();
        
        String result = "unknown";
        if(packages.isEmpty() && idList.isProject(Utility.parseId(idString))) {
            packages.addAll(Tree.getPackagesNames(idList.getProject(Utility.parseId(idString)).getPackages()));
        }
        
        if(!name.matches("(([a-zA-Z][a-zA-Z0-9]*)(\\.)?)+")) {
            result = "wrongname";
        } else if(packages.contains(name)) {
            result = "used";
        } else {
            result = "ok";
        }
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("tree/classname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightClassName(
            @Context HttpServletRequest request,
            @QueryParam("package_id") String idString,
            @QueryParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(401).build();
                
        String result = "unknown";
        String javaname = name;
        javaname += ".java";
        javaname = javaname.toLowerCase();
        
        int packageid = Utility.parseId(idString);
        if(idList.isFile(packageid))
            packageid = idList.getFile(packageid).getPackageId();
        else if(idList.isProject(packageid))
            packageid = idList.getProject(packageid).getPackageInstance(idList, "!default_package").getId();
        List<TreeFile> package_files = idList.getPackage(packageid).getFiles();
        Boolean exist = false;
        for(TreeFile file : package_files) {
            if(file.getName().toLowerCase().equals(javaname)) {
                exist = true;
                break;
            }
        }
        
        if(!name.matches("([a-zA-Z][a-zA-Z0-9_]*)")){
            result = "wrongname";
        } else if(exist) {
            result = "used";
        } else {
            result = "ok";
        }
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
           
    @POST
    @Path("tree/rename")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response renameElement(
            @Context HttpServletResponse response,
            @FormParam("id") String idString,
            @FormParam("name") String name,
            @FormParam("type") String type
            ) {
        if (idString == null || name == null || type == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        switch(type) {
            case "package":
                idList.getPackage(id).setName(name);
                break;
            case "root":
                idList.getProject(id).setName(name);
                break;
            default:
                idList.getFile(id).setName(name + ".java");
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
            return Response.status(401).build();
        
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
                packages.clear();
                packages.addAll(Tree.getPackagesNames(tpr.getPackages()));
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
    @Path("tree/filedata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileData(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        TreeFile tf;
        switch(idString) {
            case "about_tab":
                tf = new TreeFile("About", "help"); 
                break;
            case "shortcuts_tab":
                tf = new TreeFile("Shortcuts", "help"); 
                break;
            default:
                int id = Utility.parseId(idString);
                tf = idList.getFile(id);
        }
        if (tf == null)
            return Response.status(410).build();
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(tf), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("tree/revisionslist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRevisionsList(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        List<Revision> revisions = pm.getProjectTrees(Utility.parseId(idString));
        if (revisions == null)
            return Response.status(410).build();
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(revisions), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("revisions/project")
    public Response saveProjectRevision (
            @Context HttpServletRequest request
            ) {
        HttpSession session = SessionUtils.getSession(request, true);
        Long currentUserId = SessionUtils.getUserId(session);
        
        // save project to disk
        Date date = new Date();
        projectRevisions.add(date.getTime());

        ProjectRevisionSaver spr = new ProjectRevisionSaver(projectRevisions, tree, idList, files);			
        spr.saveRevision();	
        
        // save project meta info
        if (project == null) {
            project = pm.createProject(currentUserId, tree.hashes.getBranchHash(), "MyProject", null);
        } 
        Revision parentRevision = pm.findTreeByHashcode(tree.hashes.getParentTreeHash());
        pm.addTree(project.getId(), parentRevision==null?null:parentRevision.getId(), tree.hashes.getTreeHash(), date, null);
        
        String hash = tree.hashes.getBranchHash() + tree.hashes.getTreeHash();
        
        return Response.ok(hash, MediaType.TEXT_PLAIN).build();
    }
    
    @GET
    @Path("revisions/project")
    public Response getProject(
            @Context HttpServletRequest request,
            @QueryParam("projecthash") String hash
            ) {
        if (hash == null)
            return Response.status(401).build();
        
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
            long time = tf.getTimeStamp();
            String text = gpr.getFile(idList.getPackage(tf.getPackageId()).getName(), id, time);
            TreeMap<Long, String> revisions = new TreeMap<>();
            revisions.put(time, text);
            files.put(id, revisions);
        }
        
        return Response.ok().build();
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
    
    @POST
    @Path("revisions")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response saveFile(
            @Context HttpServletRequest request,
            @FormParam("id") String idString,
            @FormParam("timeStamp") long timeStamp,
            @FormParam("value") String value
            ) {
        if (idString == null || timeStamp == 0 || value == null)
            return Response.status(401).build();
        
        int addResult;
        switch(idString) {
            case "about_tab":
                addResult = 406;
                break;
            case "shortcuts_tab":
                addResult = 406;
                break;
            default:
                int id = Utility.parseId(idString);
                Revisions revisions = new Revisions(idList, files);
                addResult = revisions.addFileRevision(id, timeStamp, value);
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
            return Response.status(401).build();
        
        FileRevision fr;
        String text;
        Gson gson = new GsonBuilder().create();
        switch(idString) {
            case "about_tab":
                text = GetProjectRevision.readFile(prefix + sep + "static" + sep + "about");
                fr = new FileRevision(new Date().getTime(), text);
                break;
            case "shortcuts_tab":
                text = GetProjectRevision.readFile(prefix + sep + "static" + sep + "shortcuts");
                fr = new FileRevision(new Date().getTime(), text);
                break;
            default:
                int id = Utility.parseId(idString);
                if (idList.getFile(id) == null)
                    return Response.status(406).build();
                long time = idList.getFile(id).getTimeStamp();
                if (time == 0) {
                    fr = new FileRevision(0, "");
                } else {   
                    text = files.get(id).get(time);
                    fr = new FileRevision(time, text);
                }  
                break;
        }
        
        return Response.ok(gson.toJson(fr), MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * Compile && Executing
     */
    
    @POST
    @Path("run/compile")
    public Response compile(
            @Context HttpServletRequest request
            ) {
        ProjectRevisionSaver spr = new ProjectRevisionSaver(projectRevisions, tree, idList, files);
        srcHash = spr.saveSrc(srcHash);
        if (srcHash == null)
            return Response.status(404).build();
        
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                ArrayList<String> paths = new ArrayList<>();
                for (TreeFile tf : idList.getFileList().values()) {
                    StringBuilder path = new StringBuilder();
                    String packageName = idList.getPackage(tf.getPackageId()).getName();
                    if (packageName.startsWith("!"))
                        path.append(build).append(sep).append(srcHash).append(sep).append("src").append(sep).append(tf.getName());
                    else
                        path.append(build).append(sep).append(srcHash).append(sep).append("src").append(sep).append(packageName.replace(".", sep)).append(sep).append(tf.getName());
                    paths.add(path.toString());
                }
                
                Task task = new Task(TaskType.COMPILATION, new Compilation(paths));
                pool.add(task);
                try {
                    task.start();
                } finally {
                    Killer killer = new Killer(task);
                    killer.start();
                }

                return null;
            }
        }, LaunchPermissions.getSecureContext());
        return Response.ok().build();
    }
    
    @POST
    @Path("run/execute")
    public Response execute(
            @Context HttpServletRequest request
            ) {
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                StringBuilder path = new StringBuilder();
                String packageName = null;
                String runnableName = null;
                for (TreeFile tf : idList.getFileList().values())
                    if (tf.getType().equals("runnable")) {
                        runnableName = tf.getName();
                        if (tf.getName().endsWith(".java"))
                            runnableName = runnableName.substring(0, runnableName.length() - ".java".length());
                        packageName = idList.getPackage(tf.getPackageId()).getName();
                        packageName = packageName.startsWith("!") ? "" : packageName + ".";
                        path.append(build).append(sep).append(srcHash).append(sep).append("src").append(sep);
                        break;
                    }
                if (runnableName == null || packageName == null)
                    return null;

                Task task = new Task(TaskType.EXECUTION, new Execution("-classpath " + path.toString(), packageName + runnableName));
                pool.add(task);
                try {
                    task.start();
                } finally {
                    Killer killer = new Killer(task);
                    killer.start();
                }

                return null;
            }
        }, LaunchPermissions.getSecureContext());
        
        return Response.ok().build();
    }
    
    @POST
    @Path("run/compilerun")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response compileAndRun(
            @Context HttpServletRequest request
            ) {
        ProjectRevisionSaver spr = new ProjectRevisionSaver(projectRevisions, tree, idList, files);
        srcHash = spr.saveSrc(srcHash);
        if (srcHash == null)
            return Response.status(404).build();
        
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                
                ArrayList<String> paths = new ArrayList<>();
                StringBuilder executepath = new StringBuilder();
                String packageName = null;
                String runnableName = null;
                
                for (TreeFile file : idList.getFileList().values()) {
                    StringBuilder path = new StringBuilder();
                    path.append(build).append(sep).append(srcHash).append(sep).append("src").append(sep).append(idList.getPackage(file.getPackageId()).getName().replace(".", sep)).append(sep).append(file.getName());
                    paths.add(path.toString());
                   
                    if (file.getType().equals("runnable")) {
                        runnableName = file.getName();
                        if (file.getName().endsWith(".java"))
                            runnableName = runnableName.substring(0, runnableName.length() - ".java".length());
                        packageName = idList.getPackage(file.getPackageId()).getName();
                        executepath.append(build).append(sep).append(srcHash).append(sep).append("src").append(sep);
                        break;
                    }
                }
                
                if (runnableName == null || packageName == null)
                    return null;
                
                Task task1 = new Task(TaskType.COMPILATION, new Compilation(paths));
                pool.add(task1);
                try {
                    task1.start();
                } finally {
                    Killer killer = new Killer(task1);
                    killer.start();
                }

                try{
                    task1.join();
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
                } finally { 
                    if(!task1.isError()) {
                        Task task2 = new Task(TaskType.EXECUTION, new Execution("-classpath " + executepath.toString(), packageName + "." + runnableName));
                        pool.add(task2);
                        try {
                            task2.start();
                        } finally {
                            Killer killer = new Killer(task2);
                            killer.start();
                        }
                    }
                }
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
        Task task = pool.get(pool.size()-1);
        if (task == null)
            return Response.status(404).build();

        for (int i = 0; i < 20; i++) {
            try {
                String result;
                if ((result = task.getOutputStream()) == null) 
                    Thread.sleep(100);
                else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(result);
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, result);
                    while ((result = task.getOutputStream()) != null) {
                        list.add(result);
                        Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, result);
                    }
                    Gson gson = new GsonBuilder().create();
                    return Response.ok(gson.toJson(list), MediaType.APPLICATION_JSON).build();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.ok().build();
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
            try {
                String str = input + "\n";
                stream.write(str.getBytes());
                stream.flush();
            } catch (IOException ex) {
                Logger.getLogger(TreeService.class.getName()).log(Level.SEVERE, null, ex);
            }
        return Response.ok().build();
    }
    
}