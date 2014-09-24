package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.services.data.FileRevisions;
import com.javafiddle.web.services.data.ISessionData;
import com.javafiddle.utils.Utility;
import static com.javafiddle.tree.IdNodeType.CLASS;
import static com.javafiddle.tree.IdNodeType.PACKAGE;
import static com.javafiddle.tree.IdNodeType.PROJECT;
import com.javafiddle.tree.TreeClass;
import com.javafiddle.tree.TreeNode;
import com.javafiddle.tree.TreePackage;
import com.javafiddle.tree.TreeProject;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("tree")
@RequestScoped
public class TreeService {
    
    @Inject
    private ISessionData sd;
    
    @GET
    @Path("tree")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request
            ) {
        if(sd.getTree().isEmpty())
            Utility.addExampleTree(sd.getTree(), sd.getIdList(), sd.getFiles());
        return Response.ok(sd.getTree().toJSON(), MediaType.APPLICATION_JSON).build();
    }
        
    @POST
    @Path("addPackage")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addPackage(
            @Context HttpServletResponse response,
            @FormParam("packageName") String packageName,
            @FormParam("projectName") String projectName
            ) {
        if (packageName == null || projectName == null)
            return Response.status(401).build();
        
        TreeProject tpr = sd.getTree().getProject(sd.getIdList(), projectName);
        tpr.addPackage(sd.getIdList(), packageName);
        
        return Response.ok().build();
    }
     
    @POST
    @Path("addFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFile(
            @Context HttpServletResponse response,
            @FormParam("className") String className,
            @FormParam("packageName") String packageName,
            @FormParam("projectName") String projectName,
            @FormParam("type") String type
            ) {
        if (className == null || packageName == null || projectName == null || type == null)
            return Response.status(401).build();
        
        TreeProject project = sd.getTree().getProject(sd.getIdList(), projectName);
        TreePackage pack = project.getPackage(packageName);
        TreeClass file = pack.addFile(sd.getIdList(), type, className.endsWith(".java") ? className  : className + ".java");
        FileRevisions revisions = new FileRevisions(sd.getIdList(), sd.getFiles());
        revisions.addFileRevision(file, sd.getIdList());
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(file.getId()), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("rename")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response renameElement(
            @Context HttpServletResponse response,
            @FormParam("id") String idString,
            @FormParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        sd.getIdList().get(id).setName(name);
        
        return Response.ok().build();
    }
       
    @POST
    @Path("remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(
            @Context HttpServletRequest request,
            String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        if (!sd.getIdList().isExist(id))
            return Response.status(400).build();
        switch (sd.getIdList().getType(id)) {
            case PROJECT:
                sd.getTree().deleteProject(sd.getIdList(), id);
                break;
            case PACKAGE:
                TreePackage tp = sd.getIdList().getPackage(id);
                TreeProject tpr = sd.getIdList().getProject(tp.getProjectId());
                tpr.deletePackage(sd.getIdList(), id);
                break;
            case CLASS:
                TreeClass tf = sd.getIdList().getClass(id);
                TreePackage tpack = sd.getIdList().getPackage(tf.getPackageId());
                tpack.deleteFile(sd.getIdList(), id);
                break;
            default:
                break;
        }
        return Response.ok().build();
    }
    
    @GET
    @Path("rprojname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightProjectName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name
            ) {
        if (name == null)
            return Response.status(401).build();
        
        if(name.matches("([a-zA-Z][a-zA-Z0-9_]*)"))
            return Response.ok("\"ok\"", MediaType.APPLICATION_JSON).build();
        
        return Response.ok("\"wrongname\"", MediaType.APPLICATION_JSON).build();
    }
            
    @GET
    @Path("rpackname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightPackageName(
            @Context HttpServletRequest request,
            @QueryParam("packageName") String packageName,
            @QueryParam("projectName") String projectName
            ) {
        if (packageName == null || projectName == null)
            return Response.status(401).build();
        
        if (!packageName.matches("(([a-zA-Z][a-zA-Z0-9]*)(\\.)?)+"))
            return Response.ok("\"wrongname\"", MediaType.APPLICATION_JSON).build();

        if (packageName.endsWith("."))
            packageName = packageName.substring(0, packageName.length()-1);
        
        TreeProject project = sd.getTree().getProject(sd.getIdList(), projectName);
        if (project == null)
            return Response.ok("\"wrongprojectname\"", MediaType.APPLICATION_JSON).build();
        
        ArrayList<TreePackage> packagesList = project.getPackages();
        for (TreePackage temp : packagesList) {
            String tempName = temp.getName();
            while (tempName.contains(".")) {
                if (tempName.equals(packageName))
                    return Response.ok("\"used\"", MediaType.APPLICATION_JSON).build();
                tempName = tempName.substring(0, tempName.lastIndexOf("."));
            }
            if (tempName.equals(packageName))
                return Response.ok("\"used\"", MediaType.APPLICATION_JSON).build();
        }
        return Response.ok("\"ok\"", MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("rclassname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightClassName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam("packageName") String packageName,
            @QueryParam("projectName") String projectName
            ) {
        if (name == null || packageName == null || projectName == null)
            return Response.status(401).build();
        
        if (name.endsWith(".java"))
            name = name.substring(0, name.length()-5);
        
        if (!name.matches("([a-zA-Z][a-zA-Z0-9_]*)"))
            return Response.ok("\"wrongname\"", MediaType.APPLICATION_JSON).build();
                
        if (!name.endsWith(".java"))
            name += ".java";
                
        TreeProject project = sd.getTree().getProject(sd.getIdList(), projectName);
        if (project == null)
            return Response.ok("\"unknownproject\"", MediaType.APPLICATION_JSON).build();
        TreePackage pack = project.getPackage(packageName);
        if (pack == null)
            return Response.ok("\"unknownpack\"", MediaType.APPLICATION_JSON).build();
        List<TreeClass> filesList = pack.getFiles();
        Boolean exist = false;
        for(TreeClass file : filesList)
            if(file.getName().equals(name)) {
                exist = true;
                break;
            }
        
        if (exist)
            return Response.ok("\"used\"", MediaType.APPLICATION_JSON).build();
        return Response.ok("\"ok\"", MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("projectslist")
    public Response getProjectsList(
            @Context HttpServletRequest request
            ) {
        ArrayList<String> projects = new ArrayList<>();
        for(TreeProject temp : sd.getTree().getProjects())
            projects.add(temp.getName());

        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(projects), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("packageslist")
    public Response getPackagesList(
            @Context HttpServletRequest request,
            @QueryParam("projectname") String projectname
            ) {
        ArrayList<String> packages = new ArrayList<>();
        TreeProject project = sd.getTree().getProject(sd.getIdList(), projectname);
        if (project == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        for(TreePackage temp : project.getPackages()) 
            packages.add(temp.getName());
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(packages), MediaType.APPLICATION_JSON).build();
    }
    
    // other/temp
    //
    @GET
    @Path("revisions/lasthash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastHash(
            @Context HttpServletRequest request
            ) {
        Gson gson = new GsonBuilder().create();
        if(sd.getTree().getHashes().getHash() == null)
            return Response.ok(gson.toJson("null"), MediaType.APPLICATION_JSON).build();
        return Response.ok(gson.toJson(sd.getTree().getHashes().getHash()), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("filedata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileData(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        TreeClass tf;
        switch(idString) {
            case "about_tab":
                tf = new TreeClass("About", "help"); 
                break;
            case "shortcuts_tab":
                tf = new TreeClass("Shortcuts", "help"); 
                break;
            default:
                int id = Utility.parseId(idString);
                tf = sd.getIdList().getClass(id);
        }
        if (tf == null)
            return Response.status(410).build();
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(tf), MediaType.APPLICATION_JSON).build();
    }
        
    @GET 
    @Path("namebyid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNameByID(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        int id = Utility.parseId(idString);
        
        TreeNode tn = sd.getIdList().get(id);
        
        if (tn != null)
            return Response.ok("\"" + tn.getName() +"\"", MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
