package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.tree.Tree;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("tree")
@SessionScoped
public class TreeService implements Serializable {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request,
            @QueryParam("example") String example
            ) {
        if (example.equals("yes"))
            addFiles();
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(Tree.getInstance()), MediaType.APPLICATION_JSON).build();
    }
    
    private void addFiles() {
        Tree tree = Tree.getInstance();
        tree.add("javafiddle", "com.javafiddle.web.beans.death", "class", "CodeTextBean.java", "");
        tree.add("javafiddle", "com.javafiddle.web.projecttree.a.b.c.d.e.f.g.h.i", "class", "Reflections.java", "");
        tree.add("javafiddle", "com.javafiddle.web.beans", "class", "CommonBean.java", "");
        tree.add("javafiddle", "com.javafiddle.web.beans", "interface", "Example.txt", "");
        tree.add("javafiddle", "com.javafiddle.web.codemirror", "class", "Dummy.java", "");
        tree.add("javafiddle", "com.javafiddle.web.codemirror", "class", "FileEditions.java", "");
        tree.add("javafiddle", "com.javafiddle.web.codemirror.gui.core", "class", "ProjectEditions.java", "");
        tree.add("javafiddle", "com.javafiddle.web.codemirror.gui.core", "class", "SourceFile.java", "");
        tree.add("javafiddle", "com.javafiddle.web.codemirror.gui.core.adding", "class", "Tree.java", "");
        tree.add("javafiddle", "com.javafiddle.web.acore.appl");
        tree.add("javafiddle", "com.javafiddle.web.acore");
        tree.add("javafiddle", "com.javafiddle.web.acore.cpp");
        tree.add("javafiddle", "com.javafiddle.web.acore.cpp");
    }
}
