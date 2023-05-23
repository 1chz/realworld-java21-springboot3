package io.github.shirohoo.realworld.application.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.shirohoo.realworld.IntegrationTest;
import io.github.shirohoo.realworld.application.article.controller.CreateArticleRequest;
import io.github.shirohoo.realworld.application.article.controller.CreateCommentRequest;
import io.github.shirohoo.realworld.application.article.controller.UpdateArticleRequest;
import io.github.shirohoo.realworld.application.article.service.ArticleService;
import io.github.shirohoo.realworld.domain.article.Article;
import io.github.shirohoo.realworld.domain.article.ArticleFacets;
import io.github.shirohoo.realworld.domain.article.ArticleRepository;
import io.github.shirohoo.realworld.domain.article.ArticleVO;
import io.github.shirohoo.realworld.domain.article.Comment;
import io.github.shirohoo.realworld.domain.article.CommentRepository;
import io.github.shirohoo.realworld.domain.article.CommentVO;
import io.github.shirohoo.realworld.domain.article.Tag;
import io.github.shirohoo.realworld.domain.article.TagRepository;
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

@IntegrationTest
@DisplayName("The Article Services")
class ArticleServiceTest {
    @Autowired
    private ArticleService sut;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private Article effectiveJava;
    private User james;
    private User simpson;

    @BeforeEach
    void setUp() throws Exception {
        james = User.builder()
                .email("james@example.com")
                .username("james")
                .password("password")
                .build();
        userRepository.save(james);

        simpson = User.builder()
                .email("simpson@example.com")
                .username("simpson")
                .password("password")
                .build();
        userRepository.save(simpson);

        Tag java = new Tag("java");
        tagRepository.save(java);

        effectiveJava = Article.builder()
                .description("description")
                .title("Effective Java")
                .author(james)
                .content("content")
                .build();
        java.addTo(effectiveJava);
        articleRepository.save(effectiveJava);
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("provides a function to search articles under specific conditions.")
    void getArticles(ArticleFacets facets, int size) throws Exception {
        List<ArticleVO> articles = sut.getArticles(james, facets);
        assertThat(articles).hasSize(size);
    }

    static Stream<Arguments> getArticles() {
        return Stream.of(
                arguments(new ArticleFacets("java", null, null, 0, 20), 1),
                arguments(new ArticleFacets(null, "james", null, 0, 20), 1),
                arguments(new ArticleFacets(null, null, "simpson", 0, 20), 0),
                arguments(new ArticleFacets("java", "james", "simpson", 0, 20), 0));
    }

    @Test
    @DisplayName("provides the function to create new articles.")
    void createArticle() throws Exception {
        // given
        CreateArticleRequest request =
                new CreateArticleRequest("Test Title", "Test Description", "Test Body", List.of("test", "sample"));

        // when
        ArticleVO articleVO = sut.createArticle(james, request);

        // then
        assertThat(james.getUsername()).isEqualTo(articleVO.author().username());
        assertThat(request.title()).isEqualTo(articleVO.title());
        assertThat(request.description()).isEqualTo(articleVO.description());
        assertThat(request.body()).isEqualTo(articleVO.body());
        assertThat(request.tagList()).contains(articleVO.tagList());
    }

    @Test
    @DisplayName("provides the function to edit articles.")
    void updateArticle() throws Exception {
        // given
        Article article = Article.builder()
                .author(james)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .build();
        articleRepository.save(article);

        UpdateArticleRequest request = new UpdateArticleRequest("Updated Title", "Updated Description", "Updated Body");

        // when
        ArticleVO updatedArticleVO = sut.updateArticle(james, "test-title", request);

        // then
        assertThat(updatedArticleVO.author().username()).isEqualTo(james.getUsername());
        assertThat(updatedArticleVO.title()).isEqualTo(request.title());
        assertThat(updatedArticleVO.description()).isEqualTo(request.description());
        assertThat(updatedArticleVO.body()).isEqualTo(request.body());

        Article updatedArticle = articleRepository
                .findBySlug("updated-title")
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `updated-title`"));
        assertThat(updatedArticle.getTitle()).isEqualTo(request.title());
        assertThat(updatedArticle.getDescription()).isEqualTo(request.description());
        assertThat(updatedArticle.getContent()).isEqualTo(request.body());
    }

    @Test
    @DisplayName("prevents other users from editing articles written by them.")
    void updateArticleWithInvalidUser() throws Exception {
        // given
        UpdateArticleRequest request = new UpdateArticleRequest("Updated Title", "Updated Description", "Updated Body");

        // when
        var thrownBy = assertThatThrownBy(() -> sut.updateArticle(simpson, "effective-java", request));

        // then
        thrownBy.isInstanceOf(IllegalArgumentException.class).hasMessage("You can't edit articles written by others.");

        Article updatedArticle = articleRepository
                .findBySlug("effective-java")
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `effective-java`"));
        assertThat(updatedArticle.getTitle()).isNotEqualTo(request.title());
        assertThat(updatedArticle.getDescription()).isNotEqualTo(request.description());
        assertThat(updatedArticle.getContent()).isNotEqualTo(request.body());
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
        // when
        sut.deleteArticle(james, "effective-java");

        // then
        assertThat(articleRepository.existsByTitle("Effective Java")).isFalse();
    }

    @Test
    @DisplayName("prevents users from deleting articles made by other users.")
    void deleteArticleWithInvalidUser() throws Exception {
        // when
        var thrownBy = assertThatThrownBy(() -> sut.deleteArticle(simpson, "effective-java"));

        // then
        thrownBy.isInstanceOf(IllegalArgumentException.class)
                .hasMessage("You can't delete articles written by others.");

        assertThat(articleRepository.existsByTitle("Effective Java")).isTrue();
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

    @Test
    @DisplayName("provides the function to create a comment.")
    void createComment() throws Exception {
        // given
        CreateCommentRequest request = new CreateCommentRequest("Test Comment");

        // when
        CommentVO comment = sut.createComment(james, "effective-java", request);

        // then
        assertThat(comment.author().username()).isEqualTo(james.getUsername());
        assertThat(comment.body()).isEqualTo(request.body());
    }

    @Test
    @DisplayName("notifies the user if the article cannot be found when creating a comment.")
    void createCommentWithNonExistingArticle() throws Exception {
        // given
        CreateCommentRequest request = new CreateCommentRequest("Test Comment");

        // when
        var thrownBy = assertThatThrownBy(() -> sut.createComment(james, "non-existing-article", request));

        // then
        thrownBy.isInstanceOf(NoSuchElementException.class)
                .hasMessage("Article not found by slug: `non-existing-article`");
    }

    @Test
    @DisplayName("provides the function to get a article comments.")
    void getArticleComments() throws Exception {
        // given
        Comment comment = Comment.builder()
                .article(effectiveJava)
                .author(simpson)
                .content("Test Comment")
                .build();
        commentRepository.saveAndFlush(comment);

        // when
        List<CommentVO> comments = sut.getArticleComments(simpson, "effective-java");

        // then
        assertThat(comments.size()).isOne();
    }

    @Test
    @DisplayName("provides the function to delete a comment.")
    void deleteComment() throws Exception {
        // given
        Article article = Article.builder()
                .author(james)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .build();
        articleRepository.save(article);

        Comment comment = Comment.builder()
                .article(article)
                .author(james)
                .content("Test Comment")
                .build();

        commentRepository.save(comment);

        // when
        sut.deleteComment(james, comment.getId());

        // then
        assertThat(commentRepository.existsById(comment.getId())).isFalse();
    }

    @Test
    @DisplayName("provides the function to favorite a article.")
    void favoriteArticle() throws Exception {
        // when
        sut.favoriteArticle(simpson, effectiveJava.getSlug());

        // then
        assertThat(effectiveJava.numberOfLikes()).isOne();
    }

    @Test
    @DisplayName("provides the function to unfavorite a article.")
    void unfavoriteArticle() throws Exception {
        // when
        sut.unfavoriteArticle(simpson, effectiveJava.getSlug());

        // then
        assertThat(effectiveJava.numberOfLikes()).isZero();
    }
}
