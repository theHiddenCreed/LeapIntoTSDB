package br.com.thc.modelos;

public class DadosEntradaPipeline {
    private String symbol;
    private String type;
    private String start;
    private String end;
    private int limit;
    private int page;
    private String gran;
    private int bin;

    public DadosEntradaPipeline() {}

    private DadosEntradaPipeline(Builder builder) {
        this.symbol = builder.symbol;
        this.type = builder.type;
        this.start = builder.start;
        this.end = builder.end;
        this.limit = builder.limit;
        this.page = builder.page;
        this.gran = builder.gran;
        this.bin = builder.bin;
    }

    public static class Builder {
        private String symbol;
        private String type;
        private String start;
        private String end;
        private int limit;
        private int page;
        private String gran;
        private int bin;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder start(String start) {
            this.start = start;
            return this;
        }

        public Builder end(String end) {
            this.end = end;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder gran(String gran) {
            this.gran = gran;
            return this;
        }

        public Builder bin(int bin) {
            this.bin = bin;
            return this;
        }

        public DadosEntradaPipeline build() {
            return new DadosEntradaPipeline(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
                ", gran=" + gran +
                ", bin=" + bin +
                '}';
    }

    public String getType() {
        return this.type;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getPage() {
        return this.page;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getGran() {
        return gran;
    }

    public int getBin() {
        return bin;
    }

}
