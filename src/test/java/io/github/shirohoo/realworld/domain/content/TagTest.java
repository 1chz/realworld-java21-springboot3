package io.github.shirohoo.realworld.domain.content;

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
                .slug("test-article")
                .title("Test Article")
                .description("This is a test article.")
                .content("Lorem ipsum dolor sit amet.")
                .build();
        Tag tag = new Tag("test-tag");

        // when
        tag.addTag(article);

        // then
        assertThat(tag.articles()).containsOnly(article);
        assertThat(article.tags()).containsOnly("test-tag");
    }

    @Test
    @DisplayName("returns true if the tag names are the same.")
    void equals_returnsTrue_whenNameMatches() {
        // given
        Tag tag1 = new Tag("test-tag");
        Tag tag2 = new Tag("test-tag");

        // when
        boolean result = tag1.equals(tag2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("returns false if the tag names are different.")
    void equals_returnsFalse_whenNameDoesNotMatch() {
        // given
        Tag tag1 = new Tag("test-tag-1");
        Tag tag2 = new Tag("test-tag-2");

        // when
        boolean result = tag1.equals(tag2);

        // then
        assertThat(result).isFalse();
    }
}
