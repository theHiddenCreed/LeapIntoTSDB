package br.com.thc.rest;

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
    @Path("/docs")
    @Produces(MediaType.APPLICATION_JSON)
    public DadosSaidaPipeline docs(
        @RestQuery("symbol") String symbol, @RestQuery("type") String type, 
        @RestQuery("start") String start, @RestQuery("end") String end, 
        @RestQuery("gran") String gran, @RestQuery("bin") int bin,
        @RestQuery("page") int page, @RestQuery("limit") int limit
    ) {
        DadosEntradaPipeline dadosPipeline = DadosEntradaPipeline.builder()
            .symbol(symbol).type(type).start(start).end(end) // filtros
            .gran(gran).bin(bin) // agrupamentos
            .page(page).limit(limit) // limites
            .build();

        log.info(dadosPipeline);

        return mongoService.retornaDados(dadosPipeline, true);
    }

    @GET
    @Path("/pagination")
    @Produces(MediaType.APPLICATION_JSON)
    public DadosSaidaPipeline paginacao(
        @RestQuery("symbol") String symbol, @RestQuery("type") String type, 
        @RestQuery("start") String start, @RestQuery("end") String end, 
        @RestQuery("gran") String gran, @RestQuery("bin") int bin,
        @RestQuery("page") int page, @RestQuery("limit") int limit
    ) {
        DadosEntradaPipeline dadosPipeline = DadosEntradaPipeline.builder()
            .symbol(symbol).type(type).start(start).end(end) // filtros
            .gran(gran).bin(bin) // agrupamentos
            .page(page).limit(limit) // limites
            .build();

        log.info(dadosPipeline);

        return mongoService.retornaDados(dadosPipeline, false);
    }

}
