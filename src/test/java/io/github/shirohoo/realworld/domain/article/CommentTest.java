package io.github.shirohoo.realworld.domain.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Comment")
class CommentTest {
    @Test
    @DisplayName("returns true if the isAuthoredBy method matches the given User.")
    void isAuthoredBy_returnsTrue_whenAuthorMatches() {
        // given
        User mockUser = mock(User.class);
        Comment comment = Comment.builder()
                .id(1)
                .author(mockUser)
                .content("This is a comment.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // when
        boolean result = comment.isWritten(mockUser);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("returns false if the isAuthoredBy method does not match the given User.")
    void isAuthoredBy_returnsFalse_whenAuthorDoesNotMatch() {
        // given
        User author = mock(User.class);
        User otherUser = mock(User.class);
        Comment comment = Comment.builder()
                .id(1)
                .author(author)
                .content("This is a comment.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // when
        boolean result = comment.isWritten(otherUser);

        // then
        assertThat(result).isFalse();
    }
}
