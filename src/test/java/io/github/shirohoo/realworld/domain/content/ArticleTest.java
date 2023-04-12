package io.github.shirohoo.realworld.domain.content;

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
                .slug("how-to-write-unit-tests")
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
        assertThat(tag.articles()).contains(article);
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
        String[] tagList = article.tagList();
        assertThat(tagList).containsExactly("java", "junit", "spring");
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
        article.favoritedBy(alice);

        // then
        assertThat(article.hasFavorited(alice)).isTrue();
        assertThat(article.favoritesCount()).isEqualTo(1);
        assertThat(alice.favoritedArticles()).contains(article);
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
        article.favoritedBy(alice);

        // when
        article.unfavoritedBy(alice);

        // then
        assertThat(article.hasFavorited(alice)).isFalse();
        assertThat(article.favoritesCount()).isZero();
        assertThat(alice.favoritedArticles()).isEmpty();
    }
}
