package br.com.thc.modelos;

public class DadosEntradaPipeline {
    private String symbol;
    private String type;
    private String start;
    private String end;
    private int limit;
    private int page;

    public DadosEntradaPipeline() {}

    public DadosEntradaPipeline(String symbol, String type, String start, String end, int limit, int page) {
        this.symbol = symbol;
        this.type = type;
        this.start = start;
        this.end = end;
        this.limit = limit;
        this.page = page;
    }

    @Override
    public String toString() {
        return "DadosPipeline{" +
                "symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", limit=" + limit +
                ", page=" + page +
                '}';
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
