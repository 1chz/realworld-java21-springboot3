package io.github.shirohoo.realworld.application.content;

import io.github.shirohoo.realworld.domain.content.Articles;

import java.util.List;

record MultipleArticlesDTO(Articles[] articles, int articlesCount) {
    public MultipleArticlesDTO(List<Articles> articles) {
        this(articles.toArray(Articles[]::new), articles.size());
    }
}
