package sample.shirohoo.realworld.core.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleDetails;
import sample.shirohoo.realworld.core.model.ArticleFacets;
import sample.shirohoo.realworld.core.model.ArticleFavorite;
import sample.shirohoo.realworld.core.model.ArticleFavoriteRepository;
import sample.shirohoo.realworld.core.model.ArticleRepository;
import sample.shirohoo.realworld.core.model.SocialRepository;
import sample.shirohoo.realworld.core.model.Tag;
import sample.shirohoo.realworld.core.model.TestArticle;
import sample.shirohoo.realworld.core.model.TestUser;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserFollow;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks
    ArticleService sut;

    @Mock
    SocialRepository socialRepository;

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
    void testGetArticle_articleExists_success() {
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
    void testGetArticle_articleDoesNotExist_throwsException() {
        // given
        String slug = "test-article";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> sut.getArticle(slug));

        // then
        verify(articleRepository).findBySlug(slug);
    }

    @Test
    void testGetArticles_withFacets_returnsExpectedArticleDetails() {
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
    void testGetArticles_withUserAndFacets_returnsExpectedArticleDetails() {
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
    void getArticles_AllValid_ReturnsArticles() {
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
    void getArticles_ReturnsEmptyList_whenNoArticlesFound() {
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
    void getFeeds_withUserAndFacets_returnsExpectedArticleDetails() {
        ArticleFacets facets = new ArticleFacets(1, 5);
        Article article = new Article(testUser1, "title", "desc", "content");
        ArticleDetails expectedDetails = ArticleDetails.unauthenticated(article, 0);
        List<ArticleDetails> expectedDetailsList = List.of(expectedDetails);
        when(socialRepository.findByFollower(testUser2)).thenReturn(List.of(new UserFollow(testUser2, testUser1)));
        when(articleRepository.findByAuthors(List.of(testUser1), facets)).thenReturn(List.of(article));
        when(articleRepository.findArticleDetails(testUser2, article)).thenReturn(expectedDetails);

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getFeeds(testUser2, facets);

        // then
        assertEquals(expectedDetailsList, actualArticleDetailsList);
    }

    @Test
    void getFeeds_emptyFollowings_returnsEmptyList() {
        // given
        User user = new User("email", "username", "password");
        ArticleFacets facets = new ArticleFacets(1, 10);
        when(socialRepository.findByFollower(user)).thenReturn(List.of());
        when(articleRepository.findByAuthors(List.of(), facets)).thenReturn(List.of());

        // when
        List<ArticleDetails> actualArticleDetailsList = sut.getFeeds(user, facets);

        // then
        assertEquals(List.of(), actualArticleDetailsList);
    }

    @Test
    void testWriteArticle_success() {
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
    void testWriteArticle_nullTags_success() {
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
    void testWriteArticle_titleExists_throwsException() {
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
    void testEditDescription_success() {
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
    void testEditDescription_UserNotAuthor_throwsException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        String newDescription = "new_description";

        // when
        assertThrows(IllegalArgumentException.class, () -> sut.editDescription(testUser2, article, newDescription));

        // then
        verify(articleRepository, never()).save(article);
    }

    @Test
    void testEditContent_success() {
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
    void testEditContent_UserNotAuthor_throwsException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        String newContent = "new_content";

        // when && then
        assertThrows(IllegalArgumentException.class, () -> sut.editContent(testUser2, article, newContent));
    }

    @Test
    void delete_success() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");
        doNothing().when(articleRepository).delete(article);

        // when & then
        assertDoesNotThrow(() -> sut.delete(testUser1, article));
        verify(articleRepository).delete(article);
    }

    @Test
    void delete_RequesterNotAuthor_throwsException() {
        // given
        Article article = new Article(testUser1, "title1", "desc1", "content1");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.delete(testUser2, article));
        verify(articleRepository, never()).delete(article);
    }

    @Test
    void testIsFavorite_favoriteExists_returnTrue() {
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
    void testIsFavorite_favoriteDoesNotExist_returnFalse() {
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
    void testFavorite_Success() {
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
    void testFavorite_AlreadyFavorited_ThrowsException() {
        // given
        Article article = new TestArticle(1, testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.favorite(testUser1, article));
        verify(articleFavoriteRepository).existsBy(testUser1, article);
        verify(articleFavoriteRepository, never()).save(new ArticleFavorite(testUser1, article));
    }

    @Test
    void testUnfavorite_Success() {
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
    void testUnfavorite_AlreadyUnfavorited_ThrowsException() {
        // given
        Article article = new TestArticle(1, testUser1, "title1", "desc1", "content1");
        when(articleFavoriteRepository.existsBy(testUser1, article)).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.unfavorite(testUser1, article));
        verify(articleFavoriteRepository).existsBy(testUser1, article);
        verify(articleFavoriteRepository, never()).deleteBy(testUser1, article);
    }

    @Test
    void testGetArticleDetails_LoggedInUser_successful() {
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
    void testGetArticleDetails_NotLoggedInUser_successful() {
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
    void testEditTitle_success() {
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
    void testEditTitle_UserNotAuthor_throwsException() {
        // given
        Article article = new Article(testUser1, "title", "description", "content");
        String newTitle = "new_title";

        // when
        assertThrows(IllegalArgumentException.class, () -> sut.editTitle(testUser2, article, newTitle));

        // then
        verify(articleRepository, never()).save(article);
    }

    @Test
    void testEditTitle_ExistingTitle_throwsException() {
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
