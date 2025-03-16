package br.com.thc.mongo;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Mongo {
    private MongoDatabase mongoDatabase;

    private static final String MONGODB_CONNECTION_STRING = ConfigProvider.getConfig().getValue("MONGODB_CONNECTION_STRING", String.class);
    private static final String MONGODB_DATABASE = ConfigProvider.getConfig().getValue("MONGODB_DATABASE", String.class);

    @Inject
    Logger log;

    @Startup
    public void init() {

        ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(MONGODB_CONNECTION_STRING))
            .serverApi(serverApi)
            .build();

        MongoClient mongoClient = MongoClients.create(settings);

        this.mongoDatabase = mongoClient.getDatabase(MONGODB_DATABASE);
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.mongoDatabase.getCollection(name);
    }
}
