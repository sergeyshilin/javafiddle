package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.javafiddle.web.codemirror.Dummy;
import com.javafiddle.web.codemirror.FileEditions;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("revisions")
@SessionScoped
public class DocumentRevisionsService implements Serializable {
    Map<String, FileEditions> files;

    
    public DocumentRevisionsService() {
        files = new TreeMap<>();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRevisions(
            @Context HttpServletRequest request,
            @QueryParam("id") String id,
            @QueryParam("revision") String revision
            ) {
        switch (revision) {
            case "current":
                return Response.ok(files.get(id).getCurrentRevision(), MediaType.APPLICATION_JSON).build();
            case "last":
                return Response.ok(files.get(id).getLastRevision(), MediaType.APPLICATION_JSON).build();
            case "prev":
                return Response.ok(files.get(id).getPrevRevision(), MediaType.APPLICATION_JSON).build();
            case "next":
                return Response.ok(files.get(id).getNextRevision(), MediaType.APPLICATION_JSON).build();
        }
        return Response.noContent().build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void save(
            @Context HttpServletRequest request,
            String data
            ) {
       Dummy d = new Gson().fromJson(data, Dummy.class);
       if (!files.containsKey(d.getId()))
            files.put(d.getId(), new FileEditions());
       files.get(d.getId()).addRevision(d);
    }
    
}
