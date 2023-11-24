package sample.shirohoo.realworld.api;

import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.response.ArticleResponse;
import sample.shirohoo.realworld.api.response.SingleArticleResponse;
import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.service.ArticleService;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
public class ArticleFavoriteController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles/{slug}/favorite")
    public SingleArticleResponse doPost(Authentication authentication, @PathVariable String slug) {
        User requester = userService.getUserById(UUID.fromString(authentication.getName()));
        Article article = articleService.readArticleBySlug(slug);

        articleService.favoriteArticle(requester, article);

        return new SingleArticleResponse(this.toArticleResponse(requester, article));
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    public SingleArticleResponse doDelete(Authentication authentication, @PathVariable String slug) {
        User requester = userService.getUserById(UUID.fromString(authentication.getName()));
        Article article = articleService.readArticleBySlug(slug);

        articleService.unfavoriteArticle(requester, article);

        return new SingleArticleResponse(this.toArticleResponse(requester, article));
    }

    private ArticleResponse toArticleResponse(User user, Article article) {
        Set<ArticleTag> articleTags = articleService.getArticleTags(article);
        int countFavorites = articleService.getTotalFavorites(article);
        boolean favorited = articleService.isFavorited(user, article);
        return ArticleResponse.from(article, articleTags, favorited, countFavorites);
    }
}
