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

    private static final String MONGODB_DATABASE = ConfigProvider.getConfig().getValue("MONGODB_DATABASE", String.class);

    private static final String TIMESTAMP = "timestamp";
    private static final String SYMBOL = "metadata.symbol";
    private static final String TYPE = "metadata.type";

    private static final String MONGO_COLLECTION = "stocks";

    @Inject
    MongoClient mongoClient;

    @Inject
    Logger log;

    public DadosSaidaPipeline filterByDateMetricSymbol(DadosEntradaPipeline dadosPipeline) throws ParseException {

        Document filter = new Document(TYPE, dadosPipeline.getType())
            .append(SYMBOL, dadosPipeline.getSymbol())
            .append(TIMESTAMP, new Document("$gte", date(dadosPipeline.getStart())).append("$lte", date(dadosPipeline.getEnd())));

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

    // public DadosSaidaPipeline filterCalcDiff(DadosEntradaPipeline dadosPipeline) throws ParseException {

    //     Document intervalDate = Query.builder()
    //         .addQuery("$gte", date(dadosPipeline.getStart()))
    //         .addQuery("$lte", date(dadosPipeline.getEnd()))
    //         .build();

    //     Document filter = Query.builder()
    //         .addQuery("metadata.type", dadosPipeline.getType())
    //         .addQuery("metadata.symbol", dadosPipeline.getSymbol())
    //         .addQuery("timestamp", intervalDate)
    //         .build();

    //     Document hourParams = Query.builder()
    //         .addQuery("$hour", "$timestamp")
    //         .addQuery("$dayOfMonth", "$timestamp")
    //         .build();

    //     Document pushParams = Query.builder()
    //         .addQuery("timestamp", "$timestamp")
    //         .addQuery("value", "$value")
    //         .build();

    //     Document valuesParams = Query.builder()
    //         .addQuery("$push", pushParams)
    //         .build();

    //     Document averageParams = Query.builder()
    //         .addQuery("$avg", "$value")
    //         .addQuery("values", valuesParams)
    //         .build();

    //     Document groupsParams = Query.builder()
    //         .addQuery("symbol", "$metadata.symbol")
    //         .addQuery("hour", hourParams)
    //         .addQuery("averageValue", averageParams)
    //         .build();

    //     Document groups = Query.builder()
    //         .addQuery("_id", groupsParams)
    //         .build();

    //     Document pushParams2 = Query.builder()
    //         .addQuery("hour", "$_id.hour")
    //         .addQuery("day", "$_id.day")
    //         .addQuery("avgValue", "$averageValue")
    //         .addQuery("values", "$values")
    //         .build();

    //     Document hourlyAveragesParams = Query.builder()
    //         .addQuery("$push", pushParams2)
    //         .build();

    //     Document groups2 = Query.builder()
    //         .addQuery("_id", "$_id.symbol")
    //         .addQuery("hourlyAverages", hourlyAveragesParams)
    //         .build();

    //     Document format = Query.builder()
    //         .addQuery("_id", 0L)
    //         .build();

    //     Document sort = Query.builder().addQuery("timestamp", 1L).build();

    //     return MongoPipeline.builder(mongoClient)
    //         .setDatabase(MONGODB_DATABASE)
    //         .setCollection("stocks")
    //         .addMatch(filter)
    //         .addSort(sort)
    //         .addGroup(groups)
    //         .addGroup(groups2)
    //         .addProject(format)
    //         .execute(dadosPipeline.getPage(), dadosPipeline.getLimit());
            
    // }

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

    private Date date(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long epochTime = dateFormat.parse(date).getTime() - 10800000L;
        log.info(epochTime);
        return new Date(epochTime);
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



}
