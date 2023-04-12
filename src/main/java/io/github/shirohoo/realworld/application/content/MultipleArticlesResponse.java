package io.github.shirohoo.realworld.application.content;

import io.github.shirohoo.realworld.domain.content.ArticleVO;

import java.util.List;

public record MultipleArticlesResponse(ArticleVO[] articles, int articlesCount) {
    public MultipleArticlesResponse(List<ArticleVO> articles) {
        this(articles.toArray(ArticleVO[]::new), articles.size());
    }
}
