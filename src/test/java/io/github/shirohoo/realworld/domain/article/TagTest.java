package io.github.shirohoo.realworld.domain.article;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Tag")
class TagTest {
    @Test
    @DisplayName("adds a Tag to the given Article.")
    void addTag_addsTagToArticle() {
        // given
        Article article = Article.builder()
                .id(1)
                .title("Test Article")
                .description("This is a test article.")
                .content("Lorem ipsum dolor sit amet.")
                .build();
        Tag java = new Tag("java");

        // when
        java.tag(article);

        // then
        assertThat(article.isTaggedBy(java)).isTrue();
        assertThat(java.isTagged(article)).isTrue();
    }

    @Test
    @DisplayName("returns true if the tag names are the same.")
    void equals_returnsTrue_whenNameMatches() {
        // given
        Tag java1 = new Tag("java");
        Tag java2 = new Tag("java");

        // when
        boolean result = java1.equals(java2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("returns false if the tag names are different.")
    void equals_returnsFalse_whenNameDoesNotMatch() {
        // given
        Tag java = new Tag("java");
        Tag spring = new Tag("spring");

        // when
        boolean result = java.equals(spring);

        // then
        assertThat(result).isFalse();
    }
}
