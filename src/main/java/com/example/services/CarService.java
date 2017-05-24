package com.example.services;

import com.example.models.Time;
import com.smile.car.dtos.ComparendoDto;
import com.smile.car.dtos.PolizaDto;
import com.smile.car.dtos.PropietarioDto;
import com.smile.car.dtos.RespuestaDto;
import com.smile.car.dtos.SiniestroDto;
import com.smile.car.dtos.VehiculoDto;
import com.smile.clientWS.fasecolda.AmparoSisaType;
import com.smile.clientWS.fasecolda.ConsulExternaSIMITSisaOutType;
import com.smile.clientWS.fasecolda.ConsultaSisaRequestType;
import com.smile.clientWS.fasecolda.ConsultaSisaRespType;
import com.smile.clientWS.fasecolda.DataHeaderRequestType;
import com.smile.clientWS.fasecolda.FasecoldaService;
import com.smile.clientWS.fasecolda.FasecoldaService_Service;
import com.smile.clientWS.fasecolda.HistoricoPolizaSisaOutType;
import com.smile.clientWS.fasecolda.HistoricoSiniestroSisaOutType;
import com.smile.clientWS.fasecolda.PolizasExceptionFault;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    @Path("estudio/{placa}")
    public Response estudio(@PathParam("placa") String placa) {
        Response response = null;
        RespuestaDto respuestaDto = new RespuestaDto();
        String WSDL = "https://ambientepruebas.segurosbolivar.com/SimonWS/FasecoldaService";

        FasecoldaService_Service fasecoldaService_Service;
        FasecoldaService fasecoldaService;

        try {
            fasecoldaService_Service = new FasecoldaService_Service(new URL(WSDL));
            fasecoldaService = fasecoldaService_Service.getFasecoldaServicePort();

            ConsultaSisaRequestType sisaRequest = new ConsultaSisaRequestType();
            sisaRequest.setDataHeader(this.getHeader());
            sisaRequest.setPlaca(placa);

            ConsultaSisaRespType respType = fasecoldaService.consultaSisa(sisaRequest);
            
            List<HistoricoPolizaSisaOutType> historicosPoliza = respType.getData().getHistoricoPolizasSisa();
            if (historicosPoliza != null && !historicosPoliza.isEmpty()) {
                HistoricoPolizaSisaOutType poliza = historicosPoliza.get(0);
                respuestaDto.setVehiculo(this.obtenerDatosVehiculo(poliza));
                respuestaDto.setPoliza(this.obtenerDatosPoliza(poliza));
                respuestaDto.setPropietarioList(this.obtenerPropietarios(historicosPoliza));
                respuestaDto.setSiniestroList(this.obtenerSiniestros(
                        respType.getData().getHistoricoSiniestrosSisa()));                    
                respuestaDto.setComparendoList(this.obtenerComparendos(respType.getData().getSIMITSisa()));
            }
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(CarService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PolizasExceptionFault ex) {
            Logger.getLogger(CarService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        response = Response.status(200).entity(respuestaDto).build();
        return response;
    }

    /**
     * Retorna el DataHeader para el consumo de los servicios
     *
     * @return
     */
    private DataHeaderRequestType getHeader() {
        DataHeaderRequestType header = new DataHeaderRequestType();
        header.setModulo("2");
        header.setProceso(2000);
        header.setSubProceso(240);
        header.setCodCia(3);
        header.setCodSecc(1);
        header.setCodProducto(250);
        header.setSubProducto(1);
        header.setCodUrs("51938035");
        header.setEntidadColocadora(0);
        header.setCanal(1);
        header.setSistemaOrigen(104);
        header.setPais(1);
        header.setDireccionIp("127.0.0.1");
        header.setVersionServicio("1.0");
        return header;
    }
    
    /**
     * Obtiene los posibles propietarios del vehiculo
     * @param polizaOut
     * @return 
     */
    private List<PropietarioDto> obtenerPropietarios(List<HistoricoPolizaSisaOutType> polizasOut) {
        List<PropietarioDto> propietarios = new ArrayList<PropietarioDto>();
        
        for (HistoricoPolizaSisaOutType polizaOut : polizasOut) {
            PropietarioDto propietarioDto = new PropietarioDto();
            propietarioDto.setNombre(polizaOut.getAsegurado());
            propietarioDto.setNumeroDocumento(polizaOut.getNumeroDocumento());
            propietarioDto.setTipoDocumento(polizaOut.getTipoDocumentoAsegurado());
            propietarios.add(propietarioDto);
        }
        return propietarios;
    }
    
    /**
     * Obtiene los datos basicos del vehiculo
     * @param sisaOutType
     * @return 
     */
    private VehiculoDto obtenerDatosVehiculo(HistoricoPolizaSisaOutType historicoPolizaSisaOutType) {
        VehiculoDto vehiculoDto = new VehiculoDto();                        
        vehiculoDto.setChasis(historicoPolizaSisaOutType.getChasis());
        vehiculoDto.setClase(historicoPolizaSisaOutType.getClase());
        vehiculoDto.setMarca(historicoPolizaSisaOutType.getMarca());
        vehiculoDto.setModelo(String.valueOf(historicoPolizaSisaOutType.getModelo()));
        vehiculoDto.setMotor(historicoPolizaSisaOutType.getMotor());
        vehiculoDto.setPlaca(historicoPolizaSisaOutType.getPlaca());
        vehiculoDto.setTipo(historicoPolizaSisaOutType.getTipo());
        return vehiculoDto;        
    }
    
    /**
     * Obtiene la informacion de la ultima poliza
     * @param polizaSisaOutType
     * @return 
     */
    private PolizaDto obtenerDatosPoliza(HistoricoPolizaSisaOutType polizaSisaOutType) {
        PolizaDto polizaDto = new PolizaDto();
        polizaDto.setCodCompania(String.valueOf(polizaSisaOutType.getCodigoCompania()));
        polizaDto.setCompania(polizaSisaOutType.getNombreCompania());
        polizaDto.setFechaFinVigencia(polizaSisaOutType.getFechaFinVigencia() != null ? 
                polizaSisaOutType.getFechaFinVigencia().toGregorianCalendar().getTime() : null);
        polizaDto.setFechaVigencia(polizaSisaOutType.getFechaVigencia() != null ? 
                polizaSisaOutType.getFechaVigencia().toGregorianCalendar().getTime() : null);
        polizaDto.setNumPoliza(polizaSisaOutType.getNumeroPoliza());
        polizaDto.setValorAsegurado(polizaSisaOutType.getValorAsegurado());
        polizaDto.setVigente(polizaSisaOutType.getVigente());
        return polizaDto;
    }
    
    /**
     * Consulta los siniestros que pueda tener el vehiculo
     * @return 
     */
    private List<SiniestroDto> obtenerSiniestros(List<HistoricoSiniestroSisaOutType> siniestrosOut) {
        List<SiniestroDto> siniestros = new ArrayList<SiniestroDto>();
        if (siniestrosOut != null && !siniestrosOut.isEmpty()) {
            for (HistoricoSiniestroSisaOutType siniestroOut : siniestrosOut) {
                SiniestroDto siniestroDto = new SiniestroDto();
                siniestroDto.setNumero(siniestroOut.getNumeroSiniestro());
                siniestroDto.setFecha(siniestroOut.getFechaSiniestro() != null ? 
                        siniestroOut.getFechaSiniestro().toGregorianCalendar().getTime() : null);

                if(siniestroOut.getAmparos() != null && !siniestroOut.getAmparos().isEmpty()) {
                   AmparoSisaType amparo = siniestroOut.getAmparos().get(0);
                   siniestroDto.setValor(new BigDecimal(amparo.getValorReclamaAmparo()));
                   siniestroDto.setEstado(amparo.getEstado());
                }
                siniestros.add(siniestroDto);
            }
        }
        return siniestros;
    }
    
    /**
     * COnsulta los compatrendos que pyueda tener el vehiculo
     * @param comparendosOut
     * @return 
     */
    private List<ComparendoDto> obtenerComparendos(List<ConsulExternaSIMITSisaOutType> comparendosOut) {
        List<ComparendoDto> comparendos = new ArrayList<ComparendoDto>();
        
        if (comparendosOut != null && !comparendosOut.isEmpty()) {
            for (ConsulExternaSIMITSisaOutType comparendoOut : comparendosOut) {                
                ComparendoDto comparendoDto = new ComparendoDto();
                comparendoDto.setEstado(comparendoOut.getEstado());
                comparendoDto.setFecha(comparendoOut.getFechaComparendo() != null ? 
                        comparendoOut.getFechaComparendo().toGregorianCalendar().getTime() : null);
                comparendoDto.setValor(comparendoOut.getValorInfraccion());
                comparendoDto.setDescripcion(comparendoOut.getCodigoInfraccion());            
                comparendos.add(comparendoDto);
            }
        }
        return comparendos;
    }

    @GET
    public Time get() {
        return new Time();
    }

}
