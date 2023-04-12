package io.github.shirohoo.realworld.application.content;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleRepository;
import io.github.shirohoo.realworld.domain.content.ArticleVO;
import io.github.shirohoo.realworld.domain.content.Tag;
import io.github.shirohoo.realworld.domain.content.TagRepository;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("게시글 서비스")
class ArticleServiceTest {
    @Autowired
    private ArticleService sut;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private User james;
    private User simpson;

    @BeforeEach
    void setUp() throws Exception {
        james = new User().email("james@gmail.com").username("james");
        userRepository.save(james);

        simpson = new User().email("simpson@gmail.com").username("simpson");
        userRepository.save(james);

        Tag java = new Tag().name("java");
        tagRepository.save(java);

        Article effectiveJava =
                new Article().title("Effective Java").author(james).addTag(java).addFavoritedBy(simpson);
        articleRepository.save(effectiveJava);
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("게시글 서비스는 특정 조건으로 게시글들을 조회하는 기능을 제공한다")
    void getArticles(ArticleFacets facets) throws Exception {
        List<ArticleVO> articles = sut.getArticles(james, facets);
        assertThat(articles).hasSize(1).extracting(ArticleVO::title).containsExactly("Effective Java");
    }

    static Stream<Arguments> getArticles() {
        return Stream.of(
                Arguments.of(new ArticleFacets("java", null, null, 0, 20)),
                Arguments.of(new ArticleFacets(null, "james", null, 0, 20)),
                Arguments.of(new ArticleFacets(null, null, "simpson", 0, 20)),
                Arguments.of(new ArticleFacets("java", "james", "simpson", 0, 20)));
    }

    @Test
    @DisplayName("게시글 서비스는 새로운 게시글을 작성하는 기능을 제공한다")
    void createArticle() throws Exception {
        // given
        CreateArticleRequest request = new CreateArticleRequest(
                "Test Title", "Test Description", "Test Body", new String[] {"java", "spring"});

        // when
        ArticleVO articleVO = sut.createArticle(james, request);

        // then
        assertThat(james.username()).isEqualTo(articleVO.author().username());
        assertThat(request.title()).isEqualTo(articleVO.title());
        assertThat(request.description()).isEqualTo(articleVO.description());
        assertThat(request.body()).isEqualTo(articleVO.body());
        assertThat(request.tagList()).contains(articleVO.tagList());
    }

    @Test
    @DisplayName("게시글 서비스는 게시글을 수정하는 기능을 제공한다")
    void updateArticle() throws Exception {
        // given
        String slug = "updated-title";
        Article article = Article.builder()
                .author(james)
                .slug(slug)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .build();
        articleRepository.save(article);

        UpdateArticleRequest request = new UpdateArticleRequest("Updated Title", "Updated Description", "Updated Body");

        // when
        ArticleVO updatedArticleVO = sut.updateArticle(james, slug, request);

        // then
        assertThat(updatedArticleVO.author().username()).isEqualTo(james.username());
        assertThat(updatedArticleVO.title()).isEqualTo(request.title());
        assertThat(updatedArticleVO.description()).isEqualTo(request.description());
        assertThat(updatedArticleVO.body()).isEqualTo(request.body());

        Article updatedArticle = articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
        assertThat(updatedArticle.title()).isEqualTo(request.title());
        assertThat(updatedArticle.description()).isEqualTo(request.description());
        assertThat(updatedArticle.content()).isEqualTo(request.body());
    }

    @Test
    @DisplayName("게시글 서비스는 다른 유저가 작성한 게시글을 수정할 수 없게 한다")
    void updateArticleWithInvalidUser() throws Exception {
        // given
        String slug = "test-title";
        Article article = Article.builder()
                .author(james)
                .slug(slug)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .build();
        articleRepository.save(article);

        UpdateArticleRequest request = new UpdateArticleRequest("Updated Title", "Updated Description", "Updated Body");

        // when
        var thrownBy = assertThatThrownBy(() -> sut.updateArticle(simpson, slug, request));

        // then
        thrownBy.isInstanceOf(IllegalArgumentException.class).hasMessage("You cannot edit articles written by others.");

        Article updatedArticle = articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
        assertThat(updatedArticle.title()).isNotEqualTo(request.title());
        assertThat(updatedArticle.description()).isNotEqualTo(request.description());
        assertThat(updatedArticle.content()).isNotEqualTo(request.body());
    }

    @Test
    @DisplayName("게시글 서비스는 게시글 수정시 게시글을 찾지 못한다면 이를 유저에게 알려준다")
    void updateNonExistingArticle() throws Exception {
        // given
        String slug = "non-existing-article";
        UpdateArticleRequest request = new UpdateArticleRequest("Updated Title", "Updated Description", "Updated Body");

        // when
        var thrownBy = assertThatThrownBy(() -> sut.updateArticle(null, slug, request));

        // then
        thrownBy.isInstanceOf(NoSuchElementException.class)
                .hasMessage("Article not found by slug: `non-existing-article`");
    }

    @Test
    @DisplayName("게시글 서비스는 게시글을 삭제하는 기능을 제공한다")
    void deleteArticle() throws Exception {
        // given
        String slug = "test-article";
        Article article = Article.builder()
                .author(james)
                .slug(slug)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .build();
        articleRepository.save(article);

        // when
        sut.deleteArticle(james, slug);

        // then
        assertThat(articleRepository.existsBySlug(slug)).isFalse();
    }

    @Test
    @DisplayName("게시글 서비스는 다른 유저가 작성한 게시글을 삭제할 수 없게 한다")
    void deleteArticleWithInvalidUser() throws Exception {
        // given
        String slug = "test-article";
        Article article = Article.builder()
                .author(james)
                .slug(slug)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .build();
        articleRepository.save(article);

        // when
        var thrownBy = assertThatThrownBy(() -> sut.deleteArticle(simpson, slug));

        // then
        thrownBy.isInstanceOf(IllegalArgumentException.class)
                .hasMessage("You cannot delete articles written by others.");

        assertThat(articleRepository.existsBySlug(slug)).isTrue();
    }

    @Test
    @DisplayName("게시글 서비스는 게시글 삭제시 게시글을 찾지 못한다면 이를 유저에게 알려준다")
    void deleteNonExistingArticle() throws Exception {
        // given
        String slug = "non-existing-article";

        // when
        var thrownBy = assertThatThrownBy(() -> sut.deleteArticle(null, slug));

        // then
        thrownBy.isInstanceOf(NoSuchElementException.class)
                .hasMessage("Article not found by slug: `non-existing-article`");
    }
}
