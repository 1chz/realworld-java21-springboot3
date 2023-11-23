package sample.shirohoo.realworld.api.response;

import sample.shirohoo.realworld.core.model.ArticleComment;

public record SingleCommentResponse(ArticleCommentResponse comment) {
    public SingleCommentResponse(ArticleComment articleComment) {
        this(new ArticleCommentResponse(articleComment));
    }
}
