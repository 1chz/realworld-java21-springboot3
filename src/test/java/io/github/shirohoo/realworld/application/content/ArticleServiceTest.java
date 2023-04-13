package io.github.shirohoo.realworld.application.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleFacets;
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
@DisplayName("The Article Services")
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

    static Stream<Arguments> getArticles() {
        return Stream.of(
                Arguments.of(new ArticleFacets("java", null, null, 0, 20)),
                Arguments.of(new ArticleFacets(null, "james", null, 0, 20)),
                Arguments.of(new ArticleFacets(null, null, "simpson", 0, 20)),
                Arguments.of(new ArticleFacets("java", "james", "simpson", 0, 20)));
    }

    @BeforeEach
    void setUp() throws Exception {
        james = User.builder()
                .email("james@gmail.com")
                .username("james")
                .password("1234")
                .build();
        userRepository.save(james);

        simpson = User.builder()
                .email("simpson@gmail.com")
                .username("simpson")
                .password("1234")
                .build();
        userRepository.save(james);

        Tag java = new Tag("java");
        tagRepository.save(java);

        Article effectiveJava = Article.builder()
                .title("Effective Java")
                .author(james)
                .build()
                .addTag(java)
                .favoritedBy(simpson);
        articleRepository.save(effectiveJava);
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("provides a function to search articles under specific conditions.")
    void getArticles(ArticleFacets facets) throws Exception {
        List<ArticleVO> articles = sut.getArticles(james, facets);
        assertThat(articles).hasSize(1).extracting(ArticleVO::title).containsExactly("Effective Java");
    }

    @Test
    @DisplayName("provides the function to create new articles.")
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
    @DisplayName("provides the function to edit articles.")
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
    @DisplayName("prevents other users from editing articles written by them.")
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
    @DisplayName("notifies the user if the article cannot be found when editing the article.")
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
    @DisplayName("provides the function to delete a article.")
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
    @DisplayName("prevents users from deleting articles made by other users.")
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
    @DisplayName("notifies the user if the article cannot be found when deleting the article.")
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
