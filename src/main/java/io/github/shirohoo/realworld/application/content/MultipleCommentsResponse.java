package io.github.shirohoo.realworld.application.content;

import io.github.shirohoo.realworld.domain.content.CommentVO;

import java.util.List;

public record MultipleCommentsResponse(CommentVO[] comments) {
    public MultipleCommentsResponse(List<CommentVO> comments) {
        this(comments.toArray(CommentVO[]::new));
    }
}
