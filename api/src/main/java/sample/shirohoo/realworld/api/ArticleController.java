package sample.shirohoo.realworld.api;

import static java.util.stream.Collectors.*;

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
import sample.shirohoo.realworld.core.model.ArticleDetails;
import sample.shirohoo.realworld.core.model.ArticleFacets;
import sample.shirohoo.realworld.core.service.ArticleService;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public SingleArticleResponse doPost(Authentication authentication, @RequestBody WriteArticleRequest request) {
        var requester = userService.getUserById(UUID.fromString(authentication.getName()));
        var article = articleService.writeArticle(
                new Article(
                        requester,
                        request.article().title(),
                        request.article().description(),
                        request.article().body()),
                request.tags());

        return new SingleArticleResponse(new ArticleDetails(article, 0, false));
    }

    @GetMapping("/api/articles")
    public MultipleArticlesResponse doGet(
            Authentication authentication,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        var facets = new ArticleFacets(tag, author, favorited, offset, limit);

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return articleService.readArticles(facets).stream()
                    .map(ArticleResponse::new)
                    .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
        }

        var user = userService.getUserById(UUID.fromString(authentication.getName()));
        return articleService.readArticles(user, facets).stream()
                .map(ArticleResponse::new)
                .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
    }

    @GetMapping("/api/articles/{slug}")
    public SingleArticleResponse doGet(Authentication authentication, @PathVariable String slug) {
        var article = articleService.readArticleBySlug(slug);

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new SingleArticleResponse(articleService.getArticleInfoByAnonymous(article));
        }

        var user = userService.getUserById(UUID.fromString(authentication.getName()));
        return new SingleArticleResponse(articleService.getArticleInfoByUser(user, article));
    }

    @PutMapping("/api/articles/{slug}")
    public SingleArticleResponse doPut(
            Authentication authentication, @PathVariable String slug, @RequestBody EditArticleRequest request) {
        var requester = userService.getUserById(UUID.fromString(authentication.getName()));
        var article = articleService.readArticleBySlug(slug);

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

        var articleInfo = articleService.getArticleInfoByUser(requester, article);
        return new SingleArticleResponse(articleInfo);
    }

    @DeleteMapping("/api/articles/{slug}")
    public void doDelete(Authentication authentication, @PathVariable String slug) {
        var requester = userService.getUserById(UUID.fromString(authentication.getName()));
        var article = articleService.readArticleBySlug(slug);

        articleService.deleteArticle(requester, article);
    }

    @GetMapping("/api/articles/feed")
    public MultipleArticlesResponse doGet(
            Authentication authentication, // Must be verified
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        var facets = new ArticleFacets(offset, limit);
        var requester = userService.getUserById(UUID.fromString(authentication.getName()));

        return articleService.readFeeds(requester, facets).stream()
                .map(ArticleResponse::new)
                .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
    }
}
