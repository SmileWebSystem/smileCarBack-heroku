package com.smile.car.services;

import com.smile.car.carsmileImpl.AnalisisVehiculo;
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
        AnalisisVehiculo analisisVehiculo = new AnalisisVehiculo();
        Response response = null;
        RespuestaDto respuestaDto = analisisVehiculo.analizarPlaca(placa);         
        response = Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "1209600")
                .entity(respuestaDto).build();
        return response;
    }

}
