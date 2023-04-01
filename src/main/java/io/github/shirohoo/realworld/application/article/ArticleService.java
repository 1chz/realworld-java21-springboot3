package io.github.shirohoo.realworld.application.article;

import io.github.shirohoo.realworld.domain.article.ArticleRepository;

import org.springframework.stereotype.Service;

@Service
class ArticleService {
    private final ArticleRepository articleRepository;

    ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
}
