package io.shirohoo.realworld.application.article.controller;

import java.util.List;

import io.shirohoo.realworld.domain.article.ArticleVO;

public record MultipleArticlesResponse(ArticleVO[] articles, int articlesCount) {
    public MultipleArticlesResponse(List<ArticleVO> articles) {
        this(articles.toArray(ArticleVO[]::new), articles.size());
    }
}
