package br.com.thc.modelos;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;

public class RespostaPipeline {
    private int total;
    private int page;
    private AggregateIterable<Document> docs;

    public RespostaPipeline(int total, int page, AggregateIterable<Document> docs) {
        this.total = total;
        this.page = page;
        this.docs = docs;
    }

    public int getTotal() {
        return this.total;
    }

    public int getPage() {
        return this.page;
    }

    public AggregateIterable<Document> getDocs() {
        return this.docs;
    }
    
}
