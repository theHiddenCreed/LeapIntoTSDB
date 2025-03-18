package br.com.thc.rest;

import java.text.ParseException;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;

import br.com.thc.modelos.DadosEntradaPipeline;
import br.com.thc.modelos.DadosSaidaPipeline;
import br.com.thc.service.MongoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/prices")
@RequestScoped
public class PricesResource {
    
    @Inject
    MongoService mongoService;

    @Inject
    Logger log;

    @GET
    @Path("bruto")
    @Produces(MediaType.APPLICATION_JSON)
    public DadosSaidaPipeline filtro(
        @RestQuery("symbol") String symbol, 
        @RestQuery("type") String type, 
        @RestQuery("start") String start, 
        @RestQuery("end") String end, 
        @RestQuery("page") int page, 
        @RestQuery("limit") int limit
    ) throws ParseException {
        DadosEntradaPipeline dadosPipeline = new DadosEntradaPipeline(symbol, type, start, end, limit, page);
        log.info(dadosPipeline);
        return mongoService.filterByDateMetricSymbol(dadosPipeline);
    }

    @GET
    @Path("/media-hora")
    @Produces(MediaType.APPLICATION_JSON)
    public DadosSaidaPipeline diff(
        @RestQuery("symbol") String symbol, 
        @RestQuery("type") String type, 
        @RestQuery("start") String start, 
        @RestQuery("end") String end, 
        @RestQuery("page") int page, 
        @RestQuery("limit") int limit
    ) throws ParseException {
        DadosEntradaPipeline dadosPipeline = new DadosEntradaPipeline(symbol, type, start, end, limit, page);
        log.info(dadosPipeline);
        return mongoService.filterCalcDiff(dadosPipeline);
    }

}
