package com.example.services;

import com.example.models.Time;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/car")
@Produces(MediaType.APPLICATION_JSON)
public class CarService {
    
    @GET    
    @Path("prueba")
    public Response prueba() {
        String result = "prueba OK";
        return Response.status(200).entity(result).build();
    }

    @GET
    public Time get() {
        return new Time();
    }

}

