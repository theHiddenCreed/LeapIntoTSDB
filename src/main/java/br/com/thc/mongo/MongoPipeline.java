package br.com.thc.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.eclipse.microprofile.config.ConfigProvider;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import br.com.thc.modelos.DadosSaidaPipeline;
import io.quarkus.logging.Log;

public class MongoPipeline {

	private static final int QUERY_LIMIT = ConfigProvider.getConfig().getValue("QUERY_LIMIT", Integer.class);

	private MongoPipeline() {}

	public static PipelineBuilder builder(MongoClient client) {
		return new PipelineBuilder(client);
	}
	public static class PipelineBuilder {

		private static final String FIELD_LIMIT = "$limit";
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

		public DadosSaidaPipeline execute(int page, int limit, boolean withDocs) {
			MongoCollection<Document> mongoCollection = mongoClient.getDatabase(database).getCollection(collection);
			int total = 0;

			List<Document> pipelineCount = new ArrayList<>();
			pipelineCount.addAll(pipeline);
			pipelineCount.add(new Document("$count", "total"));
			AggregateIterable<Document> resultCount = mongoCollection.aggregate(pipelineCount);
			
			try {
				total = resultCount.first().getInteger("total");
			} catch (NullPointerException e) {
				Log.warn("Consulta vazia: " + e.getMessage());
			}

			if (limit > QUERY_LIMIT) {
				throw new LimiteExcedidoException("Limite excedido, valor recomendado é " + QUERY_LIMIT);
			} 

			int totalPaginas = Math.floorDiv(total, limit);

			if (totalPaginas < (total * limit)) {
				totalPaginas += 1;
			}

			if (withDocs) {
				if (limit > total) {
					pipeline.add(new Document("$skip", 0 * limit));
					if (total > 0) {
						pipeline.add(new Document(FIELD_LIMIT, total));
					} else {
						pipeline.add(new Document(FIELD_LIMIT, 1));
					}
	
					AggregateIterable<Document> result = mongoCollection.aggregate(pipeline);
					List<Document> documents = new ArrayList<>();
					result.forEach(documents::add);
		
					return new DadosSaidaPipeline(total, totalPaginas, 1, documents);
				} else {
					pipeline.add(new Document("$skip", (page - 1) * limit));
					pipeline.add(new Document(FIELD_LIMIT, limit));
	
					AggregateIterable<Document> result = mongoCollection.aggregate(pipeline);
					List<Document> documents = new ArrayList<>();
					result.forEach(documents::add);
		
					return new DadosSaidaPipeline(total, totalPaginas, page, documents);
				}
			} else {
				return new DadosSaidaPipeline(total, totalPaginas, page, new ArrayList<>());
			}
		}
	}

	public static class LimiteExcedidoException extends RuntimeException {
		public LimiteExcedidoException(String message) {
			super(message);
		}
	}
}