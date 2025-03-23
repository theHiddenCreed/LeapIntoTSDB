package br.com.thc.modelos;

import java.util.List;

import org.bson.Document;

public class DadosSaidaPipeline {
    private int totalDocs;
    private int totalPages;
    private int page;
    private List<Document> docs;

    public DadosSaidaPipeline(int totalDocs, int totalPages, int page, List<Document> docs) {
        this.totalDocs = totalDocs;
        this.totalPages = totalPages;
        this.page = page;
        this.docs = docs;
    }

    public int getTotalDocs() {
        return this.totalDocs;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public int getPage() {
        return this.page;
    }

    public List<Document> getDocs() {
        return this.docs;
    }
    
}
