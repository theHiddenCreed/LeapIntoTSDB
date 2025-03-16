package br.com.thc.rest;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;

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

    @GET
    @Path("/filtro-simples")
    @Produces(MediaType.APPLICATION_JSON)
    public AggregateIterable<Document> filtro() {
        return mongoService.getResult();
    }


}
