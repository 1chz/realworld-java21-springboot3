package sample.shirohoo.realworld.core.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleComment;
import sample.shirohoo.realworld.core.model.ArticleCommentRepository;
import sample.shirohoo.realworld.core.model.TestArticle;
import sample.shirohoo.realworld.core.model.TestUser;
import sample.shirohoo.realworld.core.model.User;

@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    @InjectMocks
    ArticleCommentService sut;

    @Mock
    ArticleCommentRepository articleCommentRepository;

    User author;
    Article article;
    User commenter;

    @BeforeEach
    void setUp() {
        author = new TestUser(UUID.randomUUID(), "author", "author", "password");
        article = new TestArticle(1, author, "title", "description", "body");
        commenter = new TestUser(UUID.randomUUID(), "commenter", "commenter", "password");
    }

    @Test
    void whenGetCommentWithInvalidId_thenShouldReturnComment() {
        // given
        int commentId = 1;
        ArticleComment comment = new ArticleComment(article, commenter, "comment");
        when(articleCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        ArticleComment result = sut.getComment(commentId);

        // then
        assertThat(result).isEqualTo(comment);
    }

    @Test
    void whenGetCommentWithInvalidId_thenShouldThrowException() {
        // given
        int commentId = 1;
        when(articleCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> sut.getComment(commentId));
    }

    @Test
    void whenWriteComment_thenShouldSaveToRepository() {
        // given
        ArticleComment comment = new ArticleComment(article, commenter, "comment");
        when(articleCommentRepository.save(comment)).thenReturn(comment);

        // when
        ArticleComment result = sut.write(comment);

        // then
        assertThat(result).isEqualTo(comment);
    }

    @Test
    void whenDeleteOwnComment_thenShouldNotThrowException() {
        // given
        ArticleComment comment = new ArticleComment(article, commenter, "comment");

        // when & then
        assertDoesNotThrow(() -> sut.delete(commenter, comment));
    }

    @Test
    void whenDeleteOthersComment_thenShouldThrowException() {
        // given
        ArticleComment comment = new ArticleComment(article, author, "comment");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.delete(commenter, comment));
    }

    @Test
    void whenGetComments_thenShouldReturnComments() {
        // given
        ArticleComment comment1 = new ArticleComment(article, commenter, "comment1");
        ArticleComment comment2 = new ArticleComment(article, commenter, "comment2");
        List<ArticleComment> comments = Arrays.asList(comment1, comment2);
        when(articleCommentRepository.findByArticle(article)).thenReturn(comments);

        // when
        List<ArticleComment> result = sut.getComments(article);

        // then
        assertThat(result.size()).isEqualTo(comments.size());
        assertThat(result).containsExactlyInAnyOrder(comment1, comment2);
    }
}
