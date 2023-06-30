package io.shirohoo.realworld.domain.article;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.shirohoo.realworld.domain.user.User;

@DisplayName("The ArticleVO")
class ArticleVOTest {
    @Test
    @DisplayName("constructor works fine.")
    void constructor() {
        // given
        User james = User.builder()
                .username("james")
                .email("james@example.com")
                .password("password")
                .build();
        Article article = Article.builder()
                .title("Test Article")
                .description("This is a test article.")
                .content("Test content.")
                .author(User.builder()
                        .username("james")
                        .bio("Test bio")
                        .image("https://test.com/image.png")
                        .build())
                .build();

        james.favorite(article);

        // when
        ArticleVO articleVO = new ArticleVO(james, article);

        // then
        assertThat(articleVO.slug()).isEqualTo("test-article");
        assertThat(articleVO.title()).isEqualTo("Test Article");
        assertThat(articleVO.description()).isEqualTo("This is a test article.");
        assertThat(articleVO.body()).isEqualTo("Test content.");
        assertThat(articleVO.tagList()).isEmpty();
        assertThat(articleVO.favorited()).isTrue();
        assertThat(articleVO.favoritesCount()).isOne();
        assertThat(articleVO.author().username()).isEqualTo("james");
        assertThat(articleVO.author().bio()).isEqualTo("Test bio");
        assertThat(articleVO.author().image()).isEqualTo("https://test.com/image.png");
        assertThat(articleVO.author().following()).isFalse();
    }
}
