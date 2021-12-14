package com.geekylikes.app.payloads.api.response;

import java.util.List;

public class NewsResponse {
//            private String status;
//            private Integer totalResults;
        private List<Article> articles;

        public NewsResponse() {
        }

        public NewsResponse(List<Article> articles) {
            this.articles = articles;
        }

        public List<Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }
}
