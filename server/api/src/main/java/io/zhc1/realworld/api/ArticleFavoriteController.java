package io.zhc1.realworld.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.response.SingleArticleResponse;
import io.zhc1.realworld.config.RealWorldAuthenticationToken;
import io.zhc1.realworld.service.ArticleService;
import io.zhc1.realworld.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleFavoriteController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles/{slug}/favorite")
    SingleArticleResponse like(RealWorldAuthenticationToken readersToken, @PathVariable String slug) {
        var reader = userService.getUser(readersToken.userId());
        var article = articleService.getArticle(slug);

        articleService.favorite(reader, article);

        return new SingleArticleResponse(articleService.getArticleDetails(reader, article));
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    SingleArticleResponse unlike(RealWorldAuthenticationToken readersToken, @PathVariable String slug) {
        var reader = userService.getUser(readersToken.userId());
        var article = articleService.getArticle(slug);

        articleService.unfavorite(reader, article);

        return new SingleArticleResponse(articleService.getArticleDetails(reader, article));
    }
}
