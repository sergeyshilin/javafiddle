package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.run.pool.Task;
import com.javafiddle.run.pool.TaskPool;
import com.javafiddle.run.pool.TaskType;
import com.javafiddle.run.Compilation;
import com.javafiddle.run.Execution;
import com.javafiddle.run.Killer;
import com.javafiddle.run.LaunchPermissions;
import com.javafiddle.data.ProjectRevisionSaver;
import com.javafiddle.web.services.data.ISessionData;
import com.javafiddle.tree.TreeClass;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("run")
@RequestScoped
public class RunService {
    
    @Inject
    private ISessionData sd;
    
    @POST
    @Path("compile")
    public Response compile(
            @Context HttpServletRequest request
            ) {
        ProjectRevisionSaver spr = new ProjectRevisionSaver(sd.getTree(), sd.getIdList(), sd.getFiles());
        sd.getTree().getHashes().setSrcHash(spr.saveSrc(sd.getTree().getHashes().getSrcHash()));
        if (sd.getTree().getHashes().getSrcHash() == null)
            return Response.status(404).build();
        
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                ArrayList<String> paths = new ArrayList<>();
                for (TreeClass tf : sd.getIdList().getFileList().values()) {
                    StringBuilder path = new StringBuilder();
                    String packageName = sd.getIdList().getPackage(tf.getPackageId()).getName();
                    if (packageName.startsWith("!"))
                        path.append(ISessionData.BUILD).append(ISessionData.SEP).append(sd.getTree().getHashes().getSrcHash()).append(ISessionData.SEP).append("src").append(ISessionData.SEP).append(tf.getName());
                    else
                        path.append(ISessionData.BUILD).append(ISessionData.SEP).append(sd.getTree().getHashes().getSrcHash()).append(ISessionData.SEP).append("src").append(ISessionData.SEP).append(packageName.replace(".", ISessionData.SEP)).append(ISessionData.SEP).append(tf.getName());
                    paths.add(path.toString());
                }
                
                Task task = new Task(TaskType.COMPILATION, new Compilation(paths));
                TaskPool.getInstance().add(task);
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
    @Path("execute")
    public Response execute(
            @Context HttpServletRequest request
            ) {
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                StringBuilder path = new StringBuilder();
                String packageName = null;
                String runnableName = null;
                for (TreeClass tf : sd.getIdList().getFileList().values())
                    if (tf.getType().equals("runnable")) {
                        runnableName = tf.getName();
                        if (tf.getName().endsWith(".java"))
                            runnableName = runnableName.substring(0, runnableName.length() - ".java".length());
                        packageName = sd.getIdList().getPackage(tf.getPackageId()).getName();
                        packageName = packageName.startsWith("!") ? "" : packageName + ".";
                        path.append(ISessionData.BUILD).append(ISessionData.SEP).append(sd.getTree().getHashes().getSrcHash()).append(ISessionData.SEP).append("src").append(ISessionData.SEP);
                        break;
                    }
                if (runnableName == null || packageName == null)
                    return null;

                Task task = new Task(TaskType.EXECUTION, new Execution("-classpath " + path.toString(), packageName + runnableName));
                TaskPool.getInstance().add(task);
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
    @Path("compilerun")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response compileAndRun(
            @Context HttpServletRequest request
            ) {
        ProjectRevisionSaver spr = new ProjectRevisionSaver(sd.getTree(), sd.getIdList(), sd.getFiles());
        sd.getTree().getHashes().setSrcHash(spr.saveSrc(sd.getTree().getHashes().getSrcHash()));
        if (sd.getTree().getHashes().getSrcHash() == null)
            return Response.status(404).build();
        
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                
                ArrayList<String> paths = new ArrayList<>();
                StringBuilder executepath = new StringBuilder();
                String packageName = null;
                String runnableName = null;
                
                for (TreeClass file : sd.getIdList().getFileList().values()) {
                    StringBuilder path = new StringBuilder();
                    path.append(ISessionData.BUILD).append(ISessionData.SEP).append(sd.getTree().getHashes().getSrcHash()).append(ISessionData.SEP).append("src").append(ISessionData.SEP).append(sd.getIdList().getPackage(file.getPackageId()).getName().replace(".", ISessionData.SEP)).append(ISessionData.SEP).append(file.getName());
                    paths.add(path.toString());
                   
                    if (file.getType().equals("runnable")) {
                        runnableName = file.getName();
                        if (file.getName().endsWith(".java"))
                            runnableName = runnableName.substring(0, runnableName.length() - ".java".length());
                        packageName = sd.getIdList().getPackage(file.getPackageId()).getName();
                        executepath.append(ISessionData.BUILD).append(ISessionData.SEP).append(sd.getTree().getHashes().getSrcHash()).append(ISessionData.SEP).append("src").append(ISessionData.SEP);
                        break;
                    }
                }
                
                if (runnableName == null || packageName == null)
                    return null;
                
                Task task1 = new Task(TaskType.COMPILATION, new Compilation(paths));
                TaskPool.getInstance().add(task1);
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
                    Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
                } finally { 
                    if(!task1.isError()) {
                        Task task2 = new Task(TaskType.EXECUTION, new Execution("-classpath " + executepath.toString(), packageName + "." + runnableName));
                        TaskPool.getInstance().add(task2);
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
    @Path("output")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutput(
            @Context HttpServletRequest request
            ){
        Task task = TaskPool.getInstance().get(TaskPool.getInstance().size()-1);
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
                Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("send")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setInput(
            @Context HttpServletRequest request,
            @FormParam("input") String input
            ) {
            
            Task task = TaskPool.getInstance().get(TaskPool.getInstance().size()-1);
            OutputStream stream = task.getInputStream();
            try {
                String str = input + "\n";
                stream.write(str.getBytes());
                stream.flush();
            } catch (IOException ex) {
                Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
            }
        return Response.ok().build();
    }
}
