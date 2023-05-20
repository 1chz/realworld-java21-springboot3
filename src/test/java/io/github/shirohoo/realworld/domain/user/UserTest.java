package io.github.shirohoo.realworld.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.article.Article;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The User")
class UserTest {
    @Test
    @DisplayName("users can follow each other.")
    void follow() {
        // given
        User james = User.builder()
                .id(UUID.randomUUID())
                .email("james@example.com")
                .username("james")
                .password("password")
                .build();
        User alice = User.builder()
                .id(UUID.randomUUID())
                .email("alice@example.com")
                .username("alice")
                .password("password")
                .build();

        // when
        james.follow(alice);

        // then
        assertThat(james.isFollowing(alice)).isTrue();
        assertThat(alice.hasFollower(james)).isTrue();
    }

    @Test
    @DisplayName("users can unfollow each other.")
    void unfollow() {
        // given
        User james = User.builder()
                .id(UUID.randomUUID())
                .email("james@example.com")
                .username("james")
                .password("password")
                .build();
        User alice = User.builder()
                .id(UUID.randomUUID())
                .email("alice@example.com")
                .username("alice")
                .password("password")
                .build();

        // when
        james.follow(alice);
        james.unfollow(alice);

        // then
        assertThat(james.isFollowing(alice)).isFalse();
        assertThat(alice.hasFollower(james)).isFalse();
    }

    @Test
    @DisplayName("users can favorite articles.")
    void favorite() {
        // given
        User james = User.builder()
                .id(UUID.randomUUID())
                .email("james@example.com")
                .username("james")
                .password("password")
                .build();
        Article article = Article.builder()
                .id(1)
                .author(james)
                .title("Article 1")
                .description("This is article 1")
                .content("This is the content of article 1.")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        article.favorite(james);

        // then
        assertThat(article.isFavoriteBy(james)).isTrue();
    }

    @Test
    @DisplayName("users can unfavorite articles.")
    void unfavorite() {
        // given
        User james = User.builder()
                .id(UUID.randomUUID())
                .email("james@example.com")
                .username("james")
                .password("password")
                .build();
        Article article = Article.builder()
                .id(1)
                .author(james)
                .title("Article 1")
                .description("This is article 1")
                .content("This is the content of article 1.")
                .createdAt(LocalDateTime.now())
                .build();

        james.favorite(article);

        // when
        james.unfavorite(article);

        // then
        assertThat(james.hasFavorite(article)).isFalse();
        assertThat(article.favoriteCount()).isZero();
    }
}
