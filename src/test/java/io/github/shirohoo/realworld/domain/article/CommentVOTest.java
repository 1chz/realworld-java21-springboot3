package io.github.shirohoo.realworld.domain.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The CommentVO")
class CommentVOTest {
    @Test
    @DisplayName("constructor works fine.")
    void constructor() {
        // given
        User james = User.builder()
                .username("james")
                .email("james@example.com")
                .password("password")
                .build();
        Comment comment = Comment.builder()
                .id(1)
                .article(mock(Article.class))
                .author(james)
                .content("Test comment")
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2022, 1, 2, 0, 0))
                .build();

        // when
        CommentVO commentVO = CommentVO.myComment(comment);

        // then
        assertThat(commentVO.id()).isEqualTo(1);
        assertThat(commentVO.createdAt()).isEqualTo(LocalDateTime.of(2022, 1, 1, 0, 0));
        assertThat(commentVO.updatedAt()).isEqualTo(LocalDateTime.of(2022, 1, 2, 0, 0));
        assertThat(commentVO.body()).isEqualTo("Test comment");
        assertThat(commentVO.author().username()).isEqualTo("james");
        assertThat(commentVO.author().following()).isFalse();
    }
}
