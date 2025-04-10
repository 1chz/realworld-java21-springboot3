package io.zhc1.realworld.api.response;

import io.zhc1.realworld.model.ArticleComment;

public record SingleCommentResponse(ArticleCommentResponse comment) {
    public SingleCommentResponse(ArticleComment articleComment) {
        this(new ArticleCommentResponse(articleComment));
    }
}
