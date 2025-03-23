package br.com.thc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.bson.Document;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoClient;

import br.com.thc.modelos.DadosEntradaPipeline;
import br.com.thc.modelos.DadosSaidaPipeline;
import br.com.thc.mongo.MongoPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoService {

    private static final String MONGODB_DATABASE = ConfigProvider.getConfig().getValue("MONGODB_DATABASE", String.class);

    private static final String TIMESTAMP = "timestamp";
    private static final String SYMBOL = "metadata.symbol";
    private static final String TYPE = "metadata.type";
    private static final String ID = "_id";
    private static final String VALUE = "$value";

    private static final String MONGO_COLLECTION = "stocks";

    @Inject
    MongoClient mongoClient;

    @Inject
    Logger log;

    public DadosSaidaPipeline retornaDados(DadosEntradaPipeline dadosPipeline, boolean withDocs) {
        Document filter = new Document(TYPE, dadosPipeline.getType())
            .append(TIMESTAMP, 
                new Document("$gte", Instant.parse(dadosPipeline.getStart()))
                    .append("$lte", Instant.parse(dadosPipeline.getEnd())));

        if (dadosPipeline.getSymbol() != null) {
            filter.append(SYMBOL, dadosPipeline.getSymbol());
        } 

        Document group = new Document(ID,
            new Document("symbol", "$" + SYMBOL)
                .append("date", new Document("$dateTrunc", new Document("date", "$" + TIMESTAMP)
                    .append("unit", dadosPipeline.getGran())
                    .append("binSize", dadosPipeline.getBin())
                    .append("timezone", "America/Sao_Paulo"))))
            .append("avg", new Document("$avg", VALUE))
            .append("sd", new Document("$stdDevSamp", VALUE))
            .append("min", new Document("$min", VALUE))
            .append("max", new Document("$max", VALUE))
            .append("n", new Document("$count", new Document()));

        Document project = new Document("symbol", "$_id.symbol")
            .append("date", "$_id.date")
            .append(ID, 0)
            .append("avg", 1)
            .append("sd", 1)
            .append("min", 1)
            .append("max", 1)
            .append("n", 1);

        Document sort = new Document("date", 1L);

        return MongoPipeline.builder(mongoClient)
            .setDatabase(MONGODB_DATABASE)
            .setCollection(MONGO_COLLECTION)
            .addMatch(filter)
            .addGroup(group)
            .addProject(project)
            .addSort(sort)
            .execute(dadosPipeline.getPage(), dadosPipeline.getLimit(), withDocs);

    }

}