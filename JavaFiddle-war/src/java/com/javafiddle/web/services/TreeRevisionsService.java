package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.web.codemirror.FileEditions;
import com.javafiddle.web.codemirror.Tree;
import java.io.Serializable;
import java.util.Map;
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
public class TreeRevisionsService implements Serializable {
    Map<String, FileEditions> files;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request
            ) {

        Gson gson = new GsonBuilder().create();
        Tree tree = new Tree();
        tree.addFile("javafiddle", "com.javafiddle.web.beans.death", "class", "CodeTextBean.java");
        tree.addFile("javafiddle", "com.javafiddle.web.beans", "class", "CommonBean.java");
        tree.addFile("javafiddle", "com.javafiddle.web.beans", "interface", "Example.txt");
        tree.addFile("javafiddle", "com.javafiddle.web.codemirror", "class", "Dummy.java");
        tree.addFile("javafiddle", "com.javafiddle.web.codemirror", "class", "FileEditions.java");
        tree.addFile("javafiddle", "com.javafiddle.web.codemirror.gui.core", "class", "ProjectEditions.java");
        tree.addFile("javafiddle", "com.javafiddle.web.codemirror.gui.core", "class", "SourceFile.java");
        tree.addFile("javafiddle", "com.javafiddle.web.codemirror.gui.core.adding", "class", "Tree.java");
        tree.addFile("javafiddle", "com.javafiddle.web.projecttree.a.b.c.d.e.f.g.h.i", "class", "Reflections.java");
        tree.addPackage("javafiddle", "com.javafiddle.core.appl.gui.work");
       
        return Response.ok(gson.toJson(tree), MediaType.APPLICATION_JSON).build();
    }
    
}
