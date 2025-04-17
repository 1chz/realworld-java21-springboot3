package io.zhc1.realworld.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.request.WriteCommentRequest;
import io.zhc1.realworld.api.response.ArticleCommentResponse;
import io.zhc1.realworld.api.response.MultipleCommentsResponse;
import io.zhc1.realworld.api.response.SingleCommentResponse;
import io.zhc1.realworld.config.RealWorldAuthenticationToken;
import io.zhc1.realworld.mixin.AuthenticationAwareMixin;
import io.zhc1.realworld.model.ArticleComment;
import io.zhc1.realworld.service.ArticleCommentService;
import io.zhc1.realworld.service.ArticleService;
import io.zhc1.realworld.service.SocialService;
import io.zhc1.realworld.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleCommentController implements AuthenticationAwareMixin {
    private final UserService userService;
    private final SocialService socialService;
    private final ArticleService articleService;
    private final ArticleCommentService articleCommentService;

    @PostMapping("/api/articles/{slug}/comments")
    SingleCommentResponse postComment(
            RealWorldAuthenticationToken commenterToken,
            @PathVariable String slug,
            @RequestBody WriteCommentRequest request) {
        var article = articleService.getArticle(slug);
        var commenter = userService.getUser(commenterToken.userId());
        var comment = articleCommentService.write(
                new ArticleComment(article, commenter, request.comment().body()));

        return new SingleCommentResponse(comment);
    }

    @GetMapping("/api/articles/{slug}/comments")
    MultipleCommentsResponse getComment(RealWorldAuthenticationToken readersToken, @PathVariable String slug) {
        var article = articleService.getArticle(slug);
        var comments = articleCommentService.getComments(article);

        if (this.isAnonymousUser(readersToken)) {
            return new MultipleCommentsResponse(
                    comments.stream().map(ArticleCommentResponse::new).toList());
        }

        var reader = userService.getUser(readersToken.userId());
        return new MultipleCommentsResponse(comments.stream()
                .map(comment ->
                        new ArticleCommentResponse(comment, socialService.isFollowing(reader, comment.getAuthor())))
                .toList());
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    void deleteComment(RealWorldAuthenticationToken commenterToken, @PathVariable("id") int commentId) {
        var commenter = userService.getUser(commenterToken.userId());
        var comment = articleCommentService.getComment(commentId);

        articleCommentService.delete(commenter, comment);
    }
}
