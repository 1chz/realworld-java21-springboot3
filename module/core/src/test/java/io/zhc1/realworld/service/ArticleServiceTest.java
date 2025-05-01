package io.zhc1.realworld.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.zhc1.realworld.model.Article;
import io.zhc1.realworld.model.ArticleDetails;
import io.zhc1.realworld.model.ArticleFacets;
import io.zhc1.realworld.model.ArticleFavorite;
import io.zhc1.realworld.model.ArticleFavoriteRepository;
import io.zhc1.realworld.model.ArticleRepository;
import io.zhc1.realworld.model.Tag;
import io.zhc1.realworld.model.TestArticle;
import io.zhc1.realworld.model.TestUser;
import io.zhc1.realworld.model.User;
import io.zhc1.realworld.model.UserFollow;
import io.zhc1.realworld.model.UserRelationshipRepository;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@DisplayName("Article - Article Management, Retrieval, and Interaction Operations")
class ArticleServiceTest {
    @InjectMocks
    ArticleService sut;

    @Mock
    UserRelationshipRepository userRelationshipRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    ArticleFavoriteRepository articleFavoriteRepository;

    User testUser1;
    User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new TestUser(UUID.randomUUID(), "testuser1", "testuser1", "password");
        testUser2 = new TestUser(UUID.randomUUID(), "testuser2", "testuser2", "password");
    }

    @Test
    @DisplayName("Get article should succeed when article exists")
    void whenGetArticleWithExistingSlug_thenShouldReturnArticle() {
        // given
        String slug = "test-article";
        Article expectedArticle = new Article(testUser1, "title", "desc", "content");
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(expectedArticle));

        // when
        Article actualArticle = sut.getArticle(slug);

        // then
        assertEquals(expectedArticle, actualArticle);
        verify(articleRepository).findBySlug(slug);
    }

    @Test
    @DisplayName("Get article should throw exception when article does not exist")
    void whenGetArticleWithNonExistingSlug_thenShouldThrowException() {
        // given
        String slug = "test-article";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> sut.getArticle(slug));

        // then
        verify(articleRepository).findBySlug(slug);
    }

    @Test
    @DisplayName("Get articles with facets should return expected article details")
    void whenGetArticlesWithFacets_thenShouldReturnExpectedArticleDetails() {
        // given
        ArticleFacets facets = new ArticleFacets(1, 5);
        Article article = new Article(testUser1, "title", "desc", "content");
        ArticleDetails expectedDetails = ArticleDetails.unauthenticated(article, 0);
        List<ArticleDetails> expectedDetailsList = List.of(expectedDetails);
        when(articleRepository.findAll(facets)).thenReturn(List.of(article));
        when(articleRepository.findArticleDetails(article)).thenReturn(expectedDetails);

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getArticles(facets);

        // then
        assertEquals(expectedDetailsList, actualArticleDetailsList);
    }

    @Test
    @DisplayName("Get articles with user and facets should return expected article details")
    void whenGetArticlesWithUserAndFacets_thenShouldReturnExpectedArticleDetails() {
        // given
        User requester = new User("requesterEmail", "requesterUsername", "requesterPassword");
        ArticleFacets facets = new ArticleFacets(1, 5);
        Article article = new Article(testUser1, "title", "desc", "content");
        ArticleDetails expectedDetails = ArticleDetails.unauthenticated(article, 0);
        List<ArticleDetails> expectedDetailsList = List.of(expectedDetails);
        when(articleRepository.findAll(facets)).thenReturn(List.of(article));
        when(articleRepository.findArticleDetails(requester, article)).thenReturn(expectedDetails);

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getArticles(requester, facets);

        // then
        assertEquals(expectedDetailsList, actualArticleDetailsList);
    }

    @Test
    @DisplayName("Get articles should return articles when all parameters are valid")
    void whenGetArticlesWithValidParameters_thenShouldReturnArticles() {
        // given
        ArticleFacets facets = new ArticleFacets(1, 10);
        Article article = new Article(testUser1, "title", "bef", "content");
        ArticleDetails articleDetails = ArticleDetails.unauthenticated(article, 0);
        List<ArticleDetails> expectedArticleDetailsList = List.of(articleDetails);
        when(articleRepository.findAll(facets)).thenReturn(List.of(article));
        when(articleRepository.findArticleDetails(article)).thenReturn(articleDetails);

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getArticles(facets);

        // then
        assertEquals(expectedArticleDetailsList, actualArticleDetailsList);
        verify(articleRepository).findAll(facets);
        verify(articleRepository).findArticleDetails(article);
    }

    @Test
    @DisplayName("Get articles should return empty list when no articles are found")
    void whenGetArticlesWithNoArticlesFound_thenShouldReturnEmptyList() {
        // given
        ArticleFacets facets = new ArticleFacets(1, 10);
        when(articleRepository.findAll(facets)).thenReturn(List.of());

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getArticles(facets);

        // then
        assertEquals(List.of(), actualArticleDetailsList);
        verify(articleRepository).findAll(facets);
    }

    @Test
    @DisplayName("Get feeds with user and facets should return expected article details")
    void whenGetFeedsWithUserAndFacets_thenShouldReturnExpectedArticleDetails() {
        ArticleFacets facets = new ArticleFacets(1, 5);
        Article article = new Article(testUser1, "title", "desc", "content");
        ArticleDetails expectedDetails = ArticleDetails.unauthenticated(article, 0);
        List<ArticleDetails> expectedDetailsList = List.of(expectedDetails);
        when(userRelationshipRepository.findByFollower(testUser2))
                .thenReturn(List.of(new UserFollow(testUser2, testUser1)));
        when(articleRepository.findByAuthors(List.of(testUser1), facets)).thenReturn(List.of(article));
        when(articleRepository.findArticleDetails(testUser2, article)).thenReturn(expectedDetails);

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getFeeds(testUser2, facets);

        // then
        assertEquals(expectedDetailsList, actualArticleDetailsList);
    }

    @Test
    @DisplayName("Get feeds should return empty list when user has no followings")
    void whenGetFeedsWithEmptyFollowings_thenShouldReturnEmptyList() {
        // given
        User user = new User("email", "username", "password");
        ArticleFacets facets = new ArticleFacets(1, 10);
        when(userRelationshipRepository.findByFollower(user)).thenReturn(List.of());
        when(articleRepository.findByAuthors(List.of(), facets)).thenReturn(List.of());

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getFeeds(user, facets);

        // then
        assertEquals(List.of(), actualArticleDetailsList);
    }

    @Test
    @DisplayName("Write article should succeed with valid inputs")
    void whenWriteArticleWithValidInputs_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        Set<Tag> tags = new HashSet<>(List.of(new Tag("tag1"), new Tag("tag2")));
        when(articleRepository.existsBy(article.getTitle())).thenReturn(false);
        when(articleRepository.save(article, tags)).thenReturn(article);

        // when
        Article returnedArticle = sut.write(article, tags);

        // then
        assertEquals(article, returnedArticle);
        verify(articleRepository).save(article, tags);
    }

    @Test
    @DisplayName("Write article should succeed with null tags")
    void whenWriteArticleWithNullTags_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        when(articleRepository.existsBy(article.getTitle())).thenReturn(false);
        when(articleRepository.save(eq(article), any(Collection.class))).thenReturn(article);

        // when
        Article returnedArticle = sut.write(article, null);

        // then
        assertEquals(article, returnedArticle);
        verify(articleRepository).save(eq(article), any(Collection.class));
    }

    @Test
    @DisplayName("Write article should throw exception when title already exists")
    void whenWriteArticleWithExistingTitle_thenShouldThrowException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        Set<Tag> tags = new HashSet<>(List.of(new Tag("tag1"), new Tag("tag2")));
        when(articleRepository.existsBy(article.getTitle())).thenReturn(true);

        // when
        assertThrows(IllegalArgumentException.class, () -> sut.write(article, tags));

        // then
        verify(articleRepository, never()).save(any(Article.class), any(Collection.class));
    }

    @Test
    @DisplayName("Edit description should succeed with valid inputs")
    void whenEditDescriptionWithValidInputs_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        String newDescription = "new_description";
        when(articleRepository.save(article)).thenReturn(article);

        // when
        Article updatedArticle = sut.editDescription(testUser1, article, newDescription);

        // then
        assertEquals(newDescription, updatedArticle.getDescription());
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("Edit description should throw exception when user is not the author")
    void whenEditDescriptionByNonAuthor_thenShouldThrowException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        String newDescription = "new_description";

        // when
        assertThrows(IllegalArgumentException.class, () -> sut.editDescription(testUser2, article, newDescription));

        // then
        verify(articleRepository, never()).save(article);
    }

    @Test
    @DisplayName("Edit content should succeed with valid inputs")
    void whenEditContentWithValidInputs_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        String newContent = "new_content";
        when(articleRepository.save(article)).thenReturn(article);

        // when
        Article updatedArticle = sut.editContent(testUser1, article, newContent);

        // then
        assertEquals(newContent, updatedArticle.getContent());
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("Edit content should throw exception when user is not the author")
    void whenEditContentByNonAuthor_thenShouldThrowException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        String newContent = "new_content";

        // when && then
        assertThrows(IllegalArgumentException.class, () -> sut.editContent(testUser2, article, newContent));
    }

    @Test
    @DisplayName("Delete article should succeed when user is the author")
    void whenDeleteArticleByAuthor_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        doNothing().when(articleRepository).delete(article);

        // when & then
        assertDoesNotThrow(() -> sut.delete(testUser1, article));
        verify(articleRepository).delete(article);
    }

    @Test
    @DisplayName("Delete article should throw exception when user is not the author")
    void whenDeleteArticleByNonAuthor_thenShouldThrowException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.delete(testUser2, article));
        verify(articleRepository, never()).delete(article);
    }

    @Test
    @DisplayName("Is favorite should return true when favorite exists")
    void whenIsFavoriteAndFavoriteExists_thenShouldReturnTrue() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(true);

        // when
        boolean isFavorite = sut.isFavorite(testUser1, article);

        // then
        assertTrue(isFavorite);
        verify(articleFavoriteRepository).existsBy(testUser1, article);
    }

    @Test
    @DisplayName("Is favorite should return false when favorite does not exist")
    void whenIsFavoriteAndFavoriteDoesNotExist_thenShouldReturnFalse() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(false);

        // when
        boolean isFavorite = sut.isFavorite(testUser1, article);

        // then
        assertFalse(isFavorite);
        verify(articleFavoriteRepository).existsBy(testUser1, article);
    }

    @Test
    @DisplayName("Favorite article should succeed when not already favorited")
    void whenFavoriteArticleNotAlreadyFavorited_thenShouldSucceed() {
        // given
        Article article = new TestArticle(1, testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(false);
        doNothing().when(articleFavoriteRepository).save(any(ArticleFavorite.class));

        // when & then
        assertDoesNotThrow(() -> sut.favorite(testUser1, article));
        verify(articleFavoriteRepository).existsBy(testUser1, article);
        verify(articleFavoriteRepository).save(new ArticleFavorite(testUser1, article));
    }

    @Test
    @DisplayName("Favorite article should throw exception when already favorited")
    void whenFavoriteArticleAlreadyFavorited_thenShouldThrowException() {
        // given
        Article article = new TestArticle(1, testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.favorite(testUser1, article));
        verify(articleFavoriteRepository).existsBy(testUser1, article);
        verify(articleFavoriteRepository, never()).save(new ArticleFavorite(testUser1, article));
    }

    @Test
    @DisplayName("Unfavorite article should succeed when already favorited")
    void whenUnfavoriteArticleAlreadyFavorited_thenShouldSucceed() {
        // given
        Article article = new TestArticle(1, testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(true);
        doNothing().when(articleFavoriteRepository).deleteBy(testUser1, article);

        // when & then
        assertDoesNotThrow(() -> sut.unfavorite(testUser1, article));
        verify(articleFavoriteRepository).existsBy(testUser1, article);
        verify(articleFavoriteRepository).deleteBy(testUser1, article);
    }

    @Test
    @DisplayName("Unfavorite article should throw exception when not favorited")
    void whenUnfavoriteArticleNotFavorited_thenShouldThrowException() {
        // given
        Article article = new TestArticle(1, testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.unfavorite(testUser1, article));
        verify(articleFavoriteRepository).existsBy(testUser1, article);
        verify(articleFavoriteRepository, never()).deleteBy(testUser1, article);
    }

    @Test
    @DisplayName("Get article details should succeed for logged in user")
    void whenGetArticleDetailsForLoggedInUser_thenShouldSucceed() {
        // given
        User requester = new User("requesterEmail", "requesterUsername", "requesterPassword");
        Article article = new Article(testUser1, "title", "desc", "content");
        ArticleDetails expectedDetails = ArticleDetails.unauthenticated(article, 0);
        when(articleRepository.findArticleDetails(requester, article)).thenReturn(expectedDetails);

        // when
        ArticleDetails actualArticleDetails = sut.getArticleDetails(requester, article);

        // then
        assertEquals(expectedDetails, actualArticleDetails);
        verify(articleRepository).findArticleDetails(requester, article);
    }

    @Test
    @DisplayName("Get article details should succeed for non-logged in user")
    void whenGetArticleDetailsForNonLoggedInUser_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title", "desc", "content");
        ArticleDetails expectedDetails = ArticleDetails.unauthenticated(article, 0);
        when(articleRepository.findArticleDetails(article)).thenReturn(expectedDetails);

        // when
        ArticleDetails actualArticleDetails = sut.getArticleDetails(article);

        // then
        assertEquals(expectedDetails, actualArticleDetails);
        verify(articleRepository).findArticleDetails(article);
    }

    @Test
    @DisplayName("Edit title should succeed with valid inputs")
    void whenEditTitleWithValidInputs_thenShouldSucceed() {
        // given
        Article article = new Article(testUser1, "title", "description", "content");
        String newTitle = "new_title";
        when(articleRepository.existsBy(newTitle)).thenReturn(false);
        when(articleRepository.save(article)).thenReturn(article);

        // when
        Article updatedArticle = sut.editTitle(testUser1, article, newTitle);

        // then
        assertEquals(newTitle, updatedArticle.getTitle());
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("Edit title should throw exception when user is not the author")
    void whenEditTitleByNonAuthor_thenShouldThrowException() {
        // given
        Article article = new Article(testUser1, "title", "description", "content");
        String newTitle = "new_title";

        // when
        assertThrows(IllegalArgumentException.class, () -> sut.editTitle(testUser2, article, newTitle));

        // then
        verify(articleRepository, never()).save(article);
    }

    @Test
    @DisplayName("Edit title should throw exception when title already exists")
    void whenEditTitleWithExistingTitle_thenShouldThrowException() {
        // given
        String newTitle = "new_title";
        Article article = new Article(testUser1, "title", "description", "content");
        when(articleRepository.existsBy(newTitle)).thenReturn(true);

        // when
        assertThrows(IllegalArgumentException.class, () -> sut.editTitle(testUser1, article, newTitle));

        // then
        verify(articleRepository, never()).save(article);
    }
}
