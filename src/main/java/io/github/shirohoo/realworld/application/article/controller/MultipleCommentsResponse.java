package io.github.shirohoo.realworld.application.article.controller;

import io.github.shirohoo.realworld.domain.article.CommentVO;

import java.util.List;

public record MultipleCommentsResponse(CommentVO[] comments) {
    public MultipleCommentsResponse(List<CommentVO> comments) {
        this(comments.toArray(CommentVO[]::new));
    }
}
