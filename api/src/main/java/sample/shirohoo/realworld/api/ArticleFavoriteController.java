package sample.shirohoo.realworld.api;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.response.SingleArticleResponse;
import sample.shirohoo.realworld.core.service.ArticleService;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleFavoriteController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles/{slug}/favorite")
    SingleArticleResponse doPost(Authentication authentication, @PathVariable String slug) {
        var requester = userService.getUserById(UUID.fromString(authentication.getName()));
        var article = articleService.readArticleBySlug(slug);

        articleService.favoriteArticle(requester, article);

        return new SingleArticleResponse(articleService.getArticleInfoByUser(requester, article));
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    SingleArticleResponse doDelete(Authentication authentication, @PathVariable String slug) {
        var requester = userService.getUserById(UUID.fromString(authentication.getName()));
        var article = articleService.readArticleBySlug(slug);

        articleService.unfavoriteArticle(requester, article);

        return new SingleArticleResponse(articleService.getArticleInfoByUser(requester, article));
    }
}
