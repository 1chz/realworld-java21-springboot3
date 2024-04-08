package sample.shirohoo.realworld.api;

import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.request.WriteCommentRequest;
import sample.shirohoo.realworld.api.response.ArticleCommentResponse;
import sample.shirohoo.realworld.api.response.MultipleCommentsResponse;
import sample.shirohoo.realworld.api.response.SingleCommentResponse;
import sample.shirohoo.realworld.core.model.ArticleComment;
import sample.shirohoo.realworld.core.service.ArticleCommentService;
import sample.shirohoo.realworld.core.service.ArticleService;
import sample.shirohoo.realworld.core.service.SocialService;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleCommentController {
    private final UserService userService;
    private final ArticleService articleService;
    private final SocialService socialService;
    private final ArticleCommentService articleCommentService;

    @PostMapping("/api/articles/{slug}/comments")
    SingleCommentResponse doPost(
            Authentication authentication, @PathVariable String slug, @RequestBody WriteCommentRequest request) {
        var article = articleService.getArticle(slug);
        var requester = userService.getUser(UUID.fromString(authentication.getName()));
        var articleComment = articleCommentService.write(
                new ArticleComment(article, requester, request.comment().body()));

        return new SingleCommentResponse(articleComment);
    }

    @GetMapping("/api/articles/{slug}/comments")
    MultipleCommentsResponse doGet(Authentication authentication, @PathVariable String slug) {
        var article = articleService.getArticle(slug);
        var articleComments = articleCommentService.getComments(article);

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new MultipleCommentsResponse(
                    articleComments.stream().map(ArticleCommentResponse::new).toList());
        }

        var requester = userService.getUser(UUID.fromString(authentication.getName()));
        return new MultipleCommentsResponse(articleComments.stream()
                .map(comment ->
                        new ArticleCommentResponse(comment, socialService.isFollowing(requester, comment.getAuthor())))
                .toList());
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    void doDelete(Authentication authentication, @PathVariable("id") int commentId) {
        var requester = userService.getUser(UUID.fromString(authentication.getName()));
        var articleComment = articleCommentService.getComment(commentId);

        articleCommentService.delete(requester, articleComment);
    }
}
