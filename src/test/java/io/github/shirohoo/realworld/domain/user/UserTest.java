package io.github.shirohoo.realworld.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.content.Article;

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
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .email("test1@example.com")
                .username("test1")
                .password("password")
                .build();
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("test2@example.com")
                .username("test2")
                .password("password")
                .build();

        // when
        user1.follow(user2);

        // then
        assertThat(user1.isFollowing(user2)).isTrue();
        assertThat(user2.followers()).containsExactly(user1);
    }

    @Test
    @DisplayName("users can unfollow each other.")
    void unfollow() {
        // given
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .email("test1@example.com")
                .username("test1")
                .password("password")
                .build();
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("test2@example.com")
                .username("test2")
                .password("password")
                .build();

        // when
        user1.follow(user2);
        user1.unfollow(user2);

        // then
        assertThat(user1.isFollowing(user2)).isFalse();
        assertThat(user2.followers()).isEmpty();
    }

    @Test
    @DisplayName("users can favorite articles.")
    void favorite() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .username("test")
                .password("password")
                .build();
        Article article = Article.builder()
                .id(1)
                .author(user)
                .slug("article-1")
                .title("Article 1")
                .description("This is article 1")
                .content("This is the content of article 1.")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        article.favoritedBy(user);

        // then
        assertThat(article.hasFavorited(user)).isTrue();
    }

    @Test
    @DisplayName("users can unfavorite articles.")
    void unfavorite() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .username("test")
                .password("password")
                .build();
        Article article = Article.builder()
                .id(1)
                .author(user)
                .slug("article-1")
                .title("Article 1")
                .description("This is article 1")
                .content("This is the content of article 1.")
                .createdAt(LocalDateTime.now())
                .build();

        user.favorite(article);

        // when
        user.unfavorite(article);

        // then
        assertThat(user.favoritedArticles()).isEmpty();
        assertThat(article.favorites()).isEmpty();
    }
}
