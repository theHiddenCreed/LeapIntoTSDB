package br.com.thc.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static final String MONGODB_DATABASE = ConfigProvider.getConfig().getValue("MONGODB_DATABASE",
            String.class);

    private static final String TIMESTAMP = "timestamp";
    private static final String SYMBOL = "metadata.symbol";
    private static final String TYPE = "metadata.type";
    private static final String ID = "_id";

    private static final String MONGO_COLLECTION = "stocks";

    @Inject
    MongoClient mongoClient;

    @Inject
    Logger log;

    public DadosSaidaPipeline filterByDateMetricSymbol(DadosEntradaPipeline dadosPipeline) throws ParseException {

        Document filter = new Document(TYPE, dadosPipeline.getType())
                .append(SYMBOL, dadosPipeline.getSymbol())
                .append(TIMESTAMP, new Document("$gte", date(dadosPipeline.getStart())).append("$lte",
                        date(dadosPipeline.getEnd())));

        Document format = new Document("_id", 0L);

        Document sort = new Document(TIMESTAMP, 1L);

        return MongoPipeline.builder(mongoClient)
                .setDatabase(MONGODB_DATABASE)
                .setCollection(MONGO_COLLECTION)
                .addMatch(filter)
                .addSort(sort)
                .addProject(format)
                .execute(dadosPipeline.getPage(), dadosPipeline.getLimit());
    }

    public DadosSaidaPipeline filterCalcDiff(DadosEntradaPipeline dadosPipeline) throws ParseException {
        Document filter = new Document(TYPE, dadosPipeline.getType())
            .append(SYMBOL, dadosPipeline.getSymbol())
            .append(TIMESTAMP, new Document("$gte", date(dadosPipeline.getStart())).append("$lte", date(dadosPipeline.getEnd())));

        Document group1 = new Document(ID, 
            new Document("symbol", "$" + SYMBOL)
                .append("day", new Document("$dayOfMonth", "$" + TIMESTAMP))
                .append("month", new Document("$month", "$" + TIMESTAMP))
                .append("hour", new Document("$hour", "$" + TIMESTAMP)))
            .append("averageValue", new Document("$avg", "$value"))
            .append("total", new Document("$count", new Document()));

        Document sort = new Document("_id.hour", 1L);

        return MongoPipeline.builder(mongoClient)
            .setDatabase(MONGODB_DATABASE)
            .setCollection(MONGO_COLLECTION)
            .addMatch(filter)
            .addGroup(group1)
            .addSort(sort)
            // .addGroup(group2)
            // .addProject(format)
            .execute(dadosPipeline.getPage(), dadosPipeline.getLimit());

    }

    private Date date(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long epochTime = dateFormat.parse(date).getTime() - 10800000L;
        log.info(epochTime);
        return new Date(epochTime);
    }

    // Arrays.asList(new Document("$match",
    // new Document("metadata.valueType", "close")
    // .append("metadata.symbol", "BBSA3")
    // .append("timestamp",
    // new Document("$gte",
    // new java.util.Date(1741737600000L))
    // .append("$lt",
    // new java.util.Date(1741910400000L)))),

    // new Document("$group",
    // new Document("_id",
    // new Document("year",
    // new Document("$year", "$timestamp"))
    // .append("month",
    // new Document("$month", "$timestamp"))
    // .append("day",
    // new Document("$dayOfMonth", "$timestamp"))
    // .append("hour",
    // new Document("$hour", "$timestamp")))
    // .append("values",
    // new Document("$push", "$value"))
    // .append("maxValue",
    // new Document("$max", "$value"))),

    // new Document("$project",
    // new Document("_id", 0L)
    // .append("timestamp",
    // new Document("$dateFromParts",
    // new Document("year", "$_id.year")
    // .append("month", "$_id.month")
    // .append("day", "$_id.day")
    // .append("hour", "$_id.hour")))
    // .append("medianValue",
    // new Document("$let",
    // new Document("vars",
    // new Document("sortedValues",
    // new Document("$sortArray",
    // new Document("input", "$values")
    // .append("sortBy", 1L))))
    // .append("in",
    // new Document("$arrayElemAt", Arrays.asList("$$sortedValues",
    // new Document("$floor",
    // new Document("$divide", Arrays.asList(new Document("$size",
    // "$$sortedValues"), 2L))))))))
    // .append("maxValue", "$maxValue")))

    // Arrays.asList(new Document("$match",
    // new Document("metadata.valueType", "close")
    // .append("metadata.symbol", "BBSA3")
    // .append("timestamp",
    // new Document("$gte",
    // new java.util.Date(1741737600000L))
    // .append("$lt",
    // new java.util.Date(1741910400000L)))),
    // new Document("$group",
    // new Document("_id",
    // new Document("year",
    // new Document("$year", "$timestamp"))
    // .append("month",
    // new Document("$month", "$timestamp"))
    // .append("day",
    // new Document("$dayOfMonth", "$timestamp"))
    // .append("hour",
    // new Document("$hour", "$timestamp")))
    // .append("values",
    // new Document("$push", "$value"))
    // .append("avgValue",
    // new Document("$avg", "$value"))
    // .append("sumValue",
    // new Document("$sum", "$value"))
    // .append("minValue",
    // new Document("$min", "$value"))
    // .append("maxValue",
    // new Document("$max", "$value"))),
    // new Document("$project",
    // new Document("_id", 0L)
    // .append("timestamp",
    // new Document("$dateFromParts",
    // new Document("year", "$_id.year")
    // .append("month", "$_id.month")
    // .append("day", "$_id.day")
    // .append("hour", "$_id.hour")))
    // .append("avgValue", 1L)
    // .append("medianValue",
    // new Document("$arrayElemAt", Arrays.asList("$values",
    // new Document("$floor",
    // new Document("$divide", Arrays.asList(new Document("$size", "$values"),
    // 2L))))))
    // .append("sumValue", 1L)
    // .append("minValue", 1L)
    // .append("maxValue", 1L)))

    // Arrays.asList(new Document("$match",
    // new Document("metadata.valueType", "close")
    // .append("timestamp",
    // new Document("$gte",
    // new java.util.Date(1741737600000L))
    // .append("$lt",
    // new java.util.Date(1741910400000L)))),
    // new Document("$set",
    // new Document("hour",
    // new Document("$dateTrunc",
    // new Document("date", "$timestamp")
    // .append("unit", "hour")))),
    // new Document("$group",
    // new Document("_id",
    // new Document("symbol", "$metadata.symbol")
    // .append("hour", "$hour"))
    // .append("mean",
    // new Document("$avg", "$value"))
    // .append("median",
    // new Document("$avg", "$value"))
    // .append("sum",
    // new Document("$sum", "$value"))
    // .append("min",
    // new Document("$min", "$value"))
    // .append("max",
    // new Document("$max", "$value"))),
    // new Document("$set",
    // new Document("timestamp", "$_id.hour")
    // .append("symbol", "$_id.symbol")),
    // new Document("$unset", "_id"))

}
