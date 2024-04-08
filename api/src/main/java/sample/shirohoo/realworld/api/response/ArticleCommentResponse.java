package sample.shirohoo.realworld.api.response;

import java.time.LocalDateTime;

import sample.shirohoo.realworld.core.model.ArticleComment;

public record ArticleCommentResponse(
        int id,
        LocalDateTime createdAt,
        // Note:
        //  Comments cannot be updated in the requirements,
        //  but for some reason they are in the API response spec.
        LocalDateTime updatedAt,
        String body,
        ProfileResponse author) {
    public ArticleCommentResponse(ArticleComment articleComment) {
        this(
                articleComment.getId(),
                articleComment.getCreatedAt(),
                articleComment.getCreatedAt(),
                articleComment.getContent(),
                ProfileResponse.from(articleComment.getAuthor()));
    }

    public ArticleCommentResponse(ArticleComment articleComment, boolean following) {
        this(
                articleComment.getId(),
                articleComment.getCreatedAt(),
                articleComment.getCreatedAt(),
                articleComment.getContent(),
                ProfileResponse.from(articleComment.getAuthor(), following));
    }
}
