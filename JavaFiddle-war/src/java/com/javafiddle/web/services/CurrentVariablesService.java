package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import java.util.ArrayList;
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

@Path("current")
@SessionScoped
public class CurrentVariablesService implements Serializable {

    String openedFileID;
    ArrayList<String> tabs = new ArrayList<>();
    
    @GET
    @Path("openedfileid")
    @Produces(MediaType.TEXT_PLAIN)
    public String getOpenedFileID(
            @Context HttpServletRequest request
            ) {
        return openedFileID;
    }
    
    @POST
    @Path("openedfileid")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void setOpenedFileID(
            @Context HttpServletRequest request,
            String id
            ) {
        openedFileID = new Gson().fromJson(id, String.class);
    }
    
    @GET
    @Path("opened")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isOpened(
            @Context HttpServletRequest request,
            @QueryParam("id") String id
            ) {
        Gson gson = new GsonBuilder().create();
        boolean result = false;
        if(tabs.contains(id))
            result = true;
        return Response.ok(gson.toJson(result), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("openedtabs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTabs(
            @Context HttpServletRequest request
            ) {
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(tabs), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("openedtabs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addTab(
            @Context HttpServletRequest request,
            String id
            ) {
        Gson gson = new GsonBuilder().create();
        if(!tabs.contains(gson.fromJson(id, String.class)))
            tabs.add(gson.fromJson(id, String.class));
    }
    
    @POST
    @Path("remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void removeTab(
            @Context HttpServletRequest request,
            String id
            ) {
        Gson gson = new GsonBuilder().create();
        tabs.remove(gson.fromJson(id, String.class));
    }
}
