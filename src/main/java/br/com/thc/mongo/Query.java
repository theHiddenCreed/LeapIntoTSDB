package br.com.thc.mongo;

import org.bson.Document;

public class Query {

    private Query () {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Document document = new Document();

        public Builder addQuery(String key, Object value) {
            this.document.append(key, value);
            return this;
        }

        public Document build() {
            return this.document;
        }

    }
}
