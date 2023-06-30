package io.shirohoo.realworld.application.article.controller;

import java.util.List;

import io.shirohoo.realworld.domain.article.CommentVO;

public record MultipleCommentsRecord(CommentVO[] comments) {
    public MultipleCommentsRecord(List<CommentVO> comments) {
        this(comments.toArray(CommentVO[]::new));
    }
}
