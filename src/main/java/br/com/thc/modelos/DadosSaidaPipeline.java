package br.com.thc.modelos;

import java.util.List;

import org.bson.Document;

public class DadosSaidaPipeline {
    private int total;
    private int page;
    private List<Document> docs;

    public DadosSaidaPipeline(int total, int page, List<Document> docs) {
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

    public List<Document> getDocs() {
        return this.docs;
    }
    
}
