package sample.shirohoo.realworld.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.request.EditArticleRequest;
import sample.shirohoo.realworld.api.request.WriteArticleRequest;
import sample.shirohoo.realworld.api.response.ArticleResponse;
import sample.shirohoo.realworld.api.response.MultipleArticlesResponse;
import sample.shirohoo.realworld.api.response.SingleArticleResponse;
import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleFacets;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.service.ArticleService;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public SingleArticleResponse doPost(Authentication authentication, @RequestBody WriteArticleRequest request) {
        User requester = userService.getUserById(UUID.fromString(authentication.getName()));
        Article article = articleService.writeArticle(new Article(
                requester,
                request.article().title(),
                request.article().description(),
                request.article().body()));
        Set<ArticleTag> articleTags = articleService.addArticleTags(article, request.tags());

        return SingleArticleResponse.from(article, articleTags, false, 0);
    }

    @GetMapping("/api/articles")
    public MultipleArticlesResponse doGet(
            Authentication authentication,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(tag, author, favorited, offset, limit);

        // Todo: You can optimize the lookup query by modifying it to bulk operation.
        List<ArticleResponse> articleResponses = articleService.readArticles(facets).stream()
                .map(article -> this.toArticleResponse(authentication, article))
                .toList();

        return new MultipleArticlesResponse(articleResponses);
    }

    @GetMapping("/api/articles/{slug}")
    public SingleArticleResponse doGet(Authentication authentication, @PathVariable String slug) {
        Article article = articleService.readArticleBySlug(slug);

        return new SingleArticleResponse(this.toArticleResponse(authentication, article));
    }

    @PutMapping("/api/articles/{slug}")
    public SingleArticleResponse doPut(
            Authentication authentication, @PathVariable String slug, @RequestBody EditArticleRequest request) {
        User requester = userService.getUserById(UUID.fromString(authentication.getName()));
        Article article = articleService.readArticleBySlug(slug);

        if (request.article().title() != null) {
            article = articleService.editTitle(
                    requester, article, request.article().title());
        }

        if (request.article().description() != null) {
            article = articleService.editDescription(
                    requester, article, request.article().description());
        }

        if (request.article().body() != null) {
            article = articleService.editContent(
                    requester, article, request.article().body());
        }

        return new SingleArticleResponse(this.toArticleResponse(requester, article));
    }

    @DeleteMapping("/api/articles/{slug}")
    public void doDelete(Authentication authentication, @PathVariable String slug) {
        User requester = userService.getUserById(UUID.fromString(authentication.getName()));
        Article article = articleService.readArticleBySlug(slug);

        articleService.deleteArticle(requester, article);
    }

    @GetMapping("/api/articles/feed")
    public MultipleArticlesResponse doGet(
            Authentication authentication,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(offset, limit);

        User requester = userService.getUserById(UUID.fromString(authentication.getName()));

        // Todo: You can optimize the lookup query by modifying it to bulk operation.
        List<ArticleResponse> articleResponses = articleService.readFeeds(requester, facets).stream()
                .map(article -> this.toArticleResponse(authentication, article))
                .toList();

        return new MultipleArticlesResponse(articleResponses);
    }

    private ArticleResponse toArticleResponse(Authentication authentication, Article article) {
        Set<ArticleTag> articleTags = articleService.getArticleTags(article);
        int countFavorites = articleService.getTotalFavorites(article);

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ArticleResponse.from(article, articleTags, false, countFavorites);
        }

        User user = userService.getUserById(UUID.fromString(authentication.getName()));
        boolean favorited = articleService.isFavorited(user, article);
        return ArticleResponse.from(article, articleTags, favorited, countFavorites);
    }

    private ArticleResponse toArticleResponse(User user, Article article) {
        Set<ArticleTag> articleTags = articleService.getArticleTags(article);
        int countFavorites = articleService.getTotalFavorites(article);
        boolean favorited = articleService.isFavorited(user, article);
        return ArticleResponse.from(article, articleTags, favorited, countFavorites);
    }
}
