package io.github.shirohoo.realworld.application.content;

import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleRepository;
import io.github.shirohoo.realworld.domain.content.Articles;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<Articles> getArticles(User me, ArticleFacets facets) {
        String tag = facets.tag();
        String author = facets.author();
        String favorited = facets.favorited();
        Pageable pageable = PageRequest.of(facets.offset(), facets.limit());
        Page<Article> articlesByFacets = articleRepository.findArticlesByFacets(tag, author, favorited, pageable);

        return articlesByFacets.getContent().stream()
                .map(article -> new Articles(me, article))
                .toList();
    }

    @Transactional(readOnly = true)
    public Articles getSingleArticle(User me, String slug) {
        return articleRepository
                .findBySlug(slug)
                .map(article -> new Articles(me, article))
                .orElseThrow(() -> new NoSuchElementException("Article not found: " + slug));
    }
}
