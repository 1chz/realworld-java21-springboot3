package io.github.shirohoo.realworld.domain.article;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Article")
class ArticleTest {
    private Article article;

    @BeforeEach
    void setUp() {
        User author = User.builder()
                .email("jane@example.com")
                .password("password")
                .username("jane")
                .bio("Hello, I'm Jane.")
                .image("https://i.pravatar.cc/150?img=32")
                .createdAt(LocalDateTime.now())
                .build();

        article = Article.builder()
                .title("How to write unit tests")
                .description("Unit testing is an essential part of software development.")
                .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();
    }

    @Test
    @DisplayName("add tag to article.")
    void addTag() {
        // given
        Tag tag = new Tag("java");

        // when
        article.addTag(tag);

        // then
        assertThat(article.isTaggedBy(tag)).isTrue();
        assertThat(tag.isTagged(article)).isTrue();
    }

    @Test
    @DisplayName("get tag list from article.")
    void tagList() {
        // given
        Tag java = new Tag("java");
        Tag spring = new Tag("spring");
        Tag junit = new Tag("junit");

        // when
        article.addTag(java);
        article.addTag(spring);
        article.addTag(junit);

        // then
        assertThat(article.isTaggedBy(java)).isTrue();
        assertThat(article.isTaggedBy(spring)).isTrue();
        assertThat(article.isTaggedBy(junit)).isTrue();
    }

    @Test
    @DisplayName("favorite article.")
    void favorite() {
        // given
        User alice = User.builder()
                .email("alice@example.com")
                .password("password")
                .username("alice")
                .bio("Hello, I'm Alice.")
                .image("https://i.pravatar.cc/150?img=1")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        article.favorite(alice);

        // then
        assertThat(article.isFavoriteBy(alice)).isTrue();
        assertThat(article.favoriteCount()).isOne();
        assertThat(alice.hasFavorite(article)).isTrue();
    }

    @Test
    @DisplayName("unfavorite article.")
    void unfavorite() {
        // given
        User alice = User.builder()
                .email("alice@example.com")
                .password("password")
                .username("alice")
                .bio("Hello, I'm Alice.")
                .image("https://i.pravatar.cc/150?img=1")
                .createdAt(LocalDateTime.now())
                .build();
        article.favorite(alice);

        // when
        article.unfavorite(alice);

        // then
        assertThat(article.isFavoriteBy(alice)).isFalse();
        assertThat(article.favoriteCount()).isZero();
        assertThat(alice.hasFavorite(article)).isFalse();
    }
}
