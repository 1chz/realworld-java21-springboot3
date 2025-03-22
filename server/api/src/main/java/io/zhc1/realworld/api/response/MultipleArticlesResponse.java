package io.zhc1.realworld.api.response;

import java.util.List;

public record MultipleArticlesResponse(List<ArticleResponse> articles, int articlesCount) {
    public MultipleArticlesResponse {
        articlesCount = articles.size();
    }

    public MultipleArticlesResponse(List<ArticleResponse> articles) {
        this(articles, articles.size());
    }
}
