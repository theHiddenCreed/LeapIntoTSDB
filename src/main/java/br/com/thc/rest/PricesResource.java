package br.com.thc.rest;

import java.text.ParseException;

import org.bson.Document;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;

import com.mongodb.client.AggregateIterable;

import br.com.thc.modelos.DadosPipeline;
import br.com.thc.modelos.RespostaPipeline;
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
    @Produces(MediaType.APPLICATION_JSON)
    public AggregateIterable<Document> filtro(
        @RestQuery("symbol") String symbol, 
        @RestQuery("type") String type, 
        @RestQuery("start") String start, 
        @RestQuery("end") String end, 
        @RestQuery("page") int page, 
        @RestQuery("limit") int limit
    ) throws ParseException {
        DadosPipeline dadosPipeline = new DadosPipeline(symbol, type, start, end, limit, page);
        log.info(dadosPipeline);
        return mongoService.filterByDateMetricSymbol(dadosPipeline);
    }


}
