package io.github.shirohoo.realworld.domain.article;

import io.github.shirohoo.realworld.domain.user.ProfileVO;
import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

public record CommentVO(int id, LocalDateTime createdAt, LocalDateTime updatedAt, String body, ProfileVO author) {
    public CommentVO(User me, Comment comment) {
        this(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getContent(),
                new ProfileVO(me, comment.getAuthor()));
    }
}
