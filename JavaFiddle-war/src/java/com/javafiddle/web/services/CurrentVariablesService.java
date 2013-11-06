package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("current")
@SessionScoped
public class CurrentVariablesService implements Serializable {

    String openedFileID;
    
    @GET
    @Path("openedfileid")
    @Produces(MediaType.TEXT_PLAIN)
    public String getOpenedFileID(
            @Context HttpServletRequest request
            ) {
        return openedFileID;
    }
    
    @POST
    @Path("setopenedfileid")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void setOpenedFileID(
            @Context HttpServletRequest request,
            String id
            ) {
        openedFileID = new Gson().fromJson(id, String.class);
    }
    
}
