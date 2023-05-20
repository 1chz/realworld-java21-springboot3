package io.github.shirohoo.realworld.domain.article;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2022, 1, 2, 0, 0))
                .build()
                .favorite(james);

        // when
        ArticleVO articleVO = new ArticleVO(james, article);

        // then
        assertThat(articleVO.slug()).isEqualTo("test-article");
        assertThat(articleVO.title()).isEqualTo("Test Article");
        assertThat(articleVO.description()).isEqualTo("This is a test article.");
        assertThat(articleVO.body()).isEqualTo("Test content.");
        assertThat(articleVO.tagList()).isEmpty();
        assertThat(articleVO.createdAt()).isEqualTo(LocalDateTime.of(2022, 1, 1, 0, 0));
        assertThat(articleVO.updatedAt()).isEqualTo(LocalDateTime.of(2022, 1, 2, 0, 0));
        assertThat(articleVO.favorited()).isTrue();
        assertThat(articleVO.favoritesCount()).isOne();
        assertThat(articleVO.author().username()).isEqualTo("james");
        assertThat(articleVO.author().bio()).isEqualTo("Test bio");
        assertThat(articleVO.author().image()).isEqualTo("https://test.com/image.png");
        assertThat(articleVO.author().following()).isFalse();
    }
}
