package br.com.thc.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import br.com.thc.modelos.RespostaPipeline;

public class MongoPipeline {

	private MongoPipeline() {}

	public static PipelineBuilder builder(MongoClient client) {
		return new PipelineBuilder(client);
	}

	public static class PipelineBuilder {
		private MongoClient mongoClient;
		private String database;
		private String collection;
		private List<Document> pipeline = new ArrayList<>();

		public PipelineBuilder(MongoClient mongoClient) {
			this.mongoClient = mongoClient;
		}

		public PipelineBuilder setDatabase(String database) {
			this.database = database;
			return this;
		}

		public PipelineBuilder setCollection(String collection) {
			this.collection = collection;
			return this;
		}

		public PipelineBuilder addMatch(Document match) {
			pipeline.add(new Document("$match", match));
			return this;
		}

		public PipelineBuilder addSort(Document sort) {
			pipeline.add(new Document("$sort", sort));
			return this;
		}

		public PipelineBuilder addGroup(Document group) {
			pipeline.add(new Document("$group", group));
			return this;
		}

		public PipelineBuilder addProject(Document project) {
			pipeline.add(new Document("$project", project));
			return this;
		}

		public PipelineBuilder addSet(Document set) {
			pipeline.add(new Document("$set", set));
			return this;
		}

		public PipelineBuilder addUnset(String unset) {
			pipeline.add(new Document("$unset", unset));
			return this;
		}

		public PipelineBuilder addUnwind(Document unwind) {
			pipeline.add(new Document("$unwind", unwind));
			return this;
		}

		// public int count() {
		// 	List<Document> pipelineCount = new ArrayList<>();
		// 	pipelineCount.addAll(pipeline);
		// 	pipelineCount.add(new Document("$count", "total"));
		// 	AggregateIterable<Document> resultCount = mongoCollection.aggregate(pipelineCount);
		// }

		public AggregateIterable<Document> execute(int page, int limit) {
			MongoCollection<Document> mongoCollection = mongoClient.getDatabase(database).getCollection(collection);

			pipeline.add(new Document("$limit", limit));
			pipeline.add(new Document("$skip", (page - 1) * limit));

			return mongoCollection.aggregate(pipeline);
		}
	}
}