package com.smile.car.services;

import com.smile.car.dtos.Time;
import com.smile.car.carsmileImpl.AnalisisVehiculoEJB;
import com.smile.car.dtos.RespuestaDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/car")
@Produces(MediaType.APPLICATION_JSON)
public class CarService {    
    
    /**
     * Realiza la consula del vehiculo por su placa
     * @param placa
     * @return 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("estudio/{placa}")
    public Response estudio(@PathParam("placa")String placa) {
        AnalisisVehiculoEJB analisisVehiculo = new AnalisisVehiculoEJB();
        Response response = null;
        RespuestaDto respuestaDto = analisisVehiculo.analizarPlaca(placa);         
        response = Response.status(200).entity(respuestaDto).build();
        
        return response;
    }

    @GET
    public Time get() {
        return new Time();
    }

}
