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

@Path("projectrevisions")
@SessionScoped
public class ProjectRevisionsService implements Serializable {
    Map<String, FileEditions> files = new TreeMap<>();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRevisions(
            @Context HttpServletRequest request,
            @QueryParam("id") String id,
            @QueryParam("revision") String revision
            ) {
        
        return Response.noContent().build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void save(
            @Context HttpServletRequest request,
            String data
            ) {
      
    }
    
}
