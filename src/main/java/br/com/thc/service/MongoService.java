package br.com.thc.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;

import br.com.thc.mongo.Mongo;
import br.com.thc.mongo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoService {

    private static final String MONGODB_DATABASE = ConfigProvider.getConfig().getValue("MONGODB_DATABASE", String.class);

    @Inject
    Mongo mongo;

    @Inject
    Logger log;

    public AggregateIterable<Document> filterByDateMetricSymbol() {

        Document filterDate = Query.builder()
            .addQuery("$gte", new Date(1741773600000L))
            .addQuery("$lte", new Date(1741946700000L))
            .build();

        Document filter = Query.builder()
            .addQuery("metadata.valueType", "close")
            .addQuery("metadata.symbol", "BBSA3")
            .addQuery("timestamp", filterDate)
            .build();

        return mongo.builder()
            .setDatabase(MONGODB_DATABASE)
            .setCollection("pricesVolume")
            .addMatch(filter)
            .execute();
    }

    // public AggregateIterable<Document> getResult() {
    //     AggregateIterable<Document> result = mongo.getCollection(MONGODB_DATABASE, "pricesVolume").aggregate(
    //         Arrays.asList(new Document("$match", 
    //             new Document("metadata.valueType", "close")
    //                     .append("metadata.symbol", "BBSA3")
    //                     .append("timestamp", 
    //             new Document("$gte", 
    //             new java.util.Date(1741773600000L))
    //                     .append("$lte", 
    //             new java.util.Date(1741946700000L))))));
        
    //     log.info(result.explain());

    //     return result;
    // }
    
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
