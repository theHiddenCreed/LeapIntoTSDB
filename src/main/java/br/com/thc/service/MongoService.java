package br.com.thc.service;

import java.util.Arrays;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.mongodb.client.AggregateIterable;

import br.com.thc.mongo.Mongo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoService {

    @Inject
    Mongo mongo;

    @Inject
    Logger log;

    public AggregateIterable<Document> getResult() {
        AggregateIterable<Document> result = mongo.getCollection("pricesVolume").aggregate(
            Arrays.asList(new Document("$match", 
                new Document("metadata.valueType", "close")
                        .append("metadata.symbol", "BBSA3")
                        .append("timestamp", 
                new Document("$gte", 
                new java.util.Date(1741773600000L))
                        .append("$lte", 
                new java.util.Date(1741946700000L))))));
        
        log.info(result.explain());

        return result;
    }

}
