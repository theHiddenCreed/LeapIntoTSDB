package br.com.thc.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;

import br.com.thc.modelos.DadosPipeline;
import br.com.thc.modelos.RespostaPipeline;
import br.com.thc.mongo.MongoPipeline;
import br.com.thc.mongo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoService {

    private static final String MONGODB_DATABASE = ConfigProvider.getConfig().getValue("MONGODB_DATABASE", String.class);
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Inject
    MongoClient mongoClient;

    @Inject
    Logger log;

    public AggregateIterable<Document> filterByDateMetricSymbol(DadosPipeline dadosPipeline) throws ParseException {

        Document intervalDate = Query.builder()
            .addQuery("$gte", date(dadosPipeline.getStart()))
            .addQuery("$lte", date(dadosPipeline.getEnd()))
            .build();

        log.info(intervalDate);

        Document filter = Query.builder()
            .addQuery("metadata.type", dadosPipeline.getType())
            .addQuery("metadata.symbol", dadosPipeline.getSymbol())
            .addQuery("timestamp", intervalDate)
            .build();

        Document format = Query.builder()
            .addQuery("_id", 0L)
            .build();

        Document sort = Query.builder().addQuery("timestamp", 1).build();

        return MongoPipeline.builder(mongoClient)
            .setDatabase(MONGODB_DATABASE)
            .setCollection("pricesVolume")
            .addMatch(filter)
            .addSort(sort)
            .addProject(format)
            .execute(dadosPipeline.getPage(), dadosPipeline.getLimit());
            
    }

    private Date date(String date) throws ParseException {
        return new Date(DATE_FORMAT.parse(date).getTime());
    }
    
    // Arrays.asList(new Document("$match", 
    // new Document("metadata.valueType", "close")
    //         .append("metadata.symbol", "BBSA3")
    //         .append("timestamp", 
    // new Document("$gte", 
    // new java.util.Date(1741737600000L))
    //             .append("$lt", 
    // new java.util.Date(1741910400000L)))), 

    // new Document("$group", 
    // new Document("_id", 
    // new Document("year", 
    // new Document("$year", "$timestamp"))
    //             .append("month", 
    // new Document("$month", "$timestamp"))
    //             .append("day", 
    // new Document("$dayOfMonth", "$timestamp"))
    //             .append("hour", 
    // new Document("$hour", "$timestamp")))
    //         .append("values", 
    // new Document("$push", "$value"))
    //         .append("maxValue", 
    // new Document("$max", "$value"))), 
    
    // new Document("$project", 
    // new Document("_id", 0L)
    //         .append("timestamp", 
    // new Document("$dateFromParts", 
    // new Document("year", "$_id.year")
    //                 .append("month", "$_id.month")
    //                 .append("day", "$_id.day")
    //                 .append("hour", "$_id.hour")))
    //         .append("medianValue", 
    // new Document("$let", 
    // new Document("vars", 
    // new Document("sortedValues", 
    // new Document("$sortArray", 
    // new Document("input", "$values")
    //                             .append("sortBy", 1L))))
    //                 .append("in", 
    // new Document("$arrayElemAt", Arrays.asList("$$sortedValues", 
    //                         new Document("$floor", 
    //                         new Document("$divide", Arrays.asList(new Document("$size", "$$sortedValues"), 2L))))))))
    //         .append("maxValue", "$maxValue")))


// Arrays.asList(new Document("$match", 
//     new Document("metadata.valueType", "close")
//             .append("metadata.symbol", "BBSA3")
//             .append("timestamp", 
//     new Document("$gte", 
//     new java.util.Date(1741737600000L))
//                 .append("$lt", 
//     new java.util.Date(1741910400000L)))), 
//     new Document("$group", 
//     new Document("_id", 
//     new Document("year", 
//     new Document("$year", "$timestamp"))
//                 .append("month", 
//     new Document("$month", "$timestamp"))
//                 .append("day", 
//     new Document("$dayOfMonth", "$timestamp"))
//                 .append("hour", 
//     new Document("$hour", "$timestamp")))
//             .append("values", 
//     new Document("$push", "$value"))
//             .append("avgValue", 
//     new Document("$avg", "$value"))
//             .append("sumValue", 
//     new Document("$sum", "$value"))
//             .append("minValue", 
//     new Document("$min", "$value"))
//             .append("maxValue", 
//     new Document("$max", "$value"))), 
//     new Document("$project", 
//     new Document("_id", 0L)
//             .append("timestamp", 
//     new Document("$dateFromParts", 
//     new Document("year", "$_id.year")
//                     .append("month", "$_id.month")
//                     .append("day", "$_id.day")
//                     .append("hour", "$_id.hour")))
//             .append("avgValue", 1L)
//             .append("medianValue", 
//     new Document("$arrayElemAt", Arrays.asList("$values", 
//                     new Document("$floor", 
//                     new Document("$divide", Arrays.asList(new Document("$size", "$values"), 2L))))))
//             .append("sumValue", 1L)
//             .append("minValue", 1L)
//             .append("maxValue", 1L)))

// Arrays.asList(new Document("$match", 
//     new Document("metadata.valueType", "close")
//             .append("timestamp", 
//     new Document("$gte", 
//     new java.util.Date(1741737600000L))
//                 .append("$lt", 
//     new java.util.Date(1741910400000L)))), 
//     new Document("$set", 
//     new Document("hour", 
//     new Document("$dateTrunc", 
//     new Document("date", "$timestamp")
//                     .append("unit", "hour")))), 
//     new Document("$group", 
//     new Document("_id", 
//     new Document("symbol", "$metadata.symbol")
//                 .append("hour", "$hour"))
//             .append("mean", 
//     new Document("$avg", "$value"))
//             .append("median", 
//     new Document("$avg", "$value"))
//             .append("sum", 
//     new Document("$sum", "$value"))
//             .append("min", 
//     new Document("$min", "$value"))
//             .append("max", 
//     new Document("$max", "$value"))), 
//     new Document("$set", 
//     new Document("timestamp", "$_id.hour")
//             .append("symbol", "$_id.symbol")), 
//     new Document("$unset", "_id"))

// Arrays.asList(new Document("$match", 
//     new Document("metadata.valueType", "close")
//             .append("timestamp", 
//     new Document("$gte", 
//     new java.util.Date(1741737600000L))
//                 .append("$lt", 
//     new java.util.Date(1741910400000L)))), 
//     new Document("$sort", 
//     new Document("timestamp", 1L)), 
//     new Document("$group", 
//     new Document("_id", 
//     new Document("symbol", "$metadata.symbol")
//                 .append("hour", 
//     new Document("$hour", "$timestamp"))
//                 .append("day", 
//     new Document("$dayOfMonth", "$timestamp")))
//             .append("averageValue", 
//     new Document("$avg", "$value"))
//             .append("values", 
//     new Document("$push", 
//     new Document("timestamp", "$timestamp")
//                     .append("value", "$value")))), 
//     new Document("$group", 
//     new Document("_id", "$_id.symbol")
//             .append("hourlyAverages", 
//     new Document("$push", 
//     new Document("hour", "$_id.hour")
//                     .append("day", "$_id.day")
//                     .append("avgValue", "$averageValue")
//                     .append("values", "$values")))), 
//     new Document("$project", 
//     new Document("symbol", "$_id")
//             .append("differences", 
//     new Document("$map", 
//     new Document("input", 
//     new Document("$range", Arrays.asList(1L, 
//                             new Document("$size", "$hourlyAverages"))))
//                     .append("as", "idx")
//                     .append("in", 
//     new Document("$let", 
//     new Document("vars", 
//     new Document("current", 
//     new Document("$arrayElemAt", Arrays.asList("$hourlyAverages", "$$idx")))
//                                 .append("previous", 
//     new Document("$arrayElemAt", Arrays.asList("$hourlyAverages", 
//                                         new Document("$subtract", Arrays.asList("$$idx", 1L))))))
//                             .append("in", 
//     new Document("timestamp", 
//     new Document("$arrayElemAt", Arrays.asList("$$current.values.timestamp", 0L)))
//                                 .append("symbol", "$symbol")
//                                 .append("diffAvg", 
//     new Document("$subtract", Arrays.asList("$$current.avgValue", "$$previous.avgValue"))))))))), 
//     new Document("$unwind", "$differences"), 
//     new Document("$project", 
//     new Document("_id", 0L)
//             .append("timestamp", "$differences.timestamp")
//             .append("symbol", "$differences.symbol")
//             .append("diffAvg", "$differences.diffAvg")))

}
