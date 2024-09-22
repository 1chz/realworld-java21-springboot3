package io.zhc1.realworld.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.response.SingleArticleResponse;
import io.zhc1.realworld.config.RealworldJwt;
import io.zhc1.realworld.core.service.ArticleService;
import io.zhc1.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleFavoriteController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles/{slug}/favorite")
    SingleArticleResponse doPost(RealworldJwt jwt, @PathVariable String slug) {
        var requester = userService.getUser(jwt.userId());
        var article = articleService.getArticle(slug);

        articleService.favorite(requester, article);

        return new SingleArticleResponse(articleService.getArticleDetails(requester, article));
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    SingleArticleResponse doDelete(RealworldJwt jwt, @PathVariable String slug) {
        var requester = userService.getUser(jwt.userId());
        var article = articleService.getArticle(slug);

        articleService.unfavorite(requester, article);

        return new SingleArticleResponse(articleService.getArticleDetails(requester, article));
    }
}
