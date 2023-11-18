package sample.shirohoo.realworld.core.service;

import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleCommentRepository;
import sample.shirohoo.realworld.core.model.ArticleFacets;
import sample.shirohoo.realworld.core.model.ArticleFavorite;
import sample.shirohoo.realworld.core.model.ArticleFavoriteRepository;
import sample.shirohoo.realworld.core.model.ArticleRepository;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.ArticleTagRepository;
import sample.shirohoo.realworld.core.model.SocialRepository;
import sample.shirohoo.realworld.core.model.Tag;
import sample.shirohoo.realworld.core.model.TagRepository;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserFollow;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final SocialRepository socialRepository;
    private final ArticleTagRepository articleTagRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;

    /**
     * Get article by slug.
     * @param slug article slug
     * @return Returns article
     */
    public Article readArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug).orElseThrow(() -> new NoSuchElementException("article not found."));
    }

    /**
     * Get articles by facets.
     * @param facets article facets
     * @return Returns articles
     */
    public List<Article> readArticles(ArticleFacets facets) {
        return articleRepository.findByFacets(facets);
    }

    /**
     * Get articles by my followings.
     * @param user user who requested
     * @param facets article facets
     * @return Returns articles
     */
    public List<Article> readFeeds(User user, ArticleFacets facets) {
        List<User> following = socialRepository.findByFollower(user).stream()
                .map(UserFollow::getFollowing)
                .toList();

        return articleRepository.findByAuthorInOrderByCreatedAtDesc(following, facets);
    }

    /**
     * Write a new article.
     * @param article article
     * @return Returns the written article
     */
    public Article writeArticle(Article article) {
        if (articleRepository.existsByTitle(article.getTitle())) {
            throw new IllegalArgumentException("title is already exists.");
        }

        return articleRepository.save(article);
    }

    /**
     * Get article's tags.
     * @param article article
     * @return Returns article's tags
     */
    public Set<ArticleTag> getArticleTags(Article article) {
        return articleTagRepository.findByArticle(article);
    }

    /**
     * Add tags to article.
     * @param article article
     * @param tags tags
     * @return Returns article's tags
     */
    public Set<ArticleTag> addArticleTags(Article article, Collection<Tag> tags) {
        // Find existing tags by name.
        Set<Tag> existingTags =
                tagRepository.findByNameIn(tags.stream().map(Tag::getName).toList());

        // Save tags that do not exist yet.
        List<Tag> newTags =
                tags.stream().filter(tag -> !existingTags.contains(tag)).collect(toList());
        tagRepository.saveAll(newTags);

        // Merge existing and new tags.
        Set<Tag> allTags = new HashSet<>(existingTags);
        allTags.addAll(newTags);

        // Find existing article tags for the given article and tags.
        Set<ArticleTag> existingArticleTags = articleTagRepository.findByArticleAndTagIn(article, allTags);

        // Save article tags for tags that do not exist in the article.
        List<ArticleTag> newArticleTags = allTags.stream()
                .filter(tag -> existingArticleTags.stream().noneMatch(tag::equalsArticleTag))
                .map(tag -> new ArticleTag(article, tag))
                .collect(toList());
        articleTagRepository.saveAll(newArticleTags);

        // Combine and return the final set of ArticleTags.
        Set<ArticleTag> allArticleTags = new HashSet<>(existingArticleTags);
        allArticleTags.addAll(newArticleTags);

        return allArticleTags;
    }

    /**
     * Edit article title.
     * @param requester user who requested
     * @param article article
     * @param title new title
     * @return Returns the edited article
     */
    public Article editTitle(User requester, Article article, String title) {
        if (article.isNotAuthor(requester)) {
            throw new IllegalArgumentException("you can't edit articles written by others.");
        }

        if (articleRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("title is already exists.");
        }

        article.setTitle(title);
        return articleRepository.save(article);
    }

    /**
     * Edit article description.
     * @param requester user who requested
     * @param article article
     * @param description new description
     * @return Returns the edited article
     */
    public Article editDescription(User requester, Article article, String description) {
        if (article.isNotAuthor(requester)) {
            throw new IllegalArgumentException("you can't edit articles written by others.");
        }

        article.setDescription(description);
        return articleRepository.save(article);
    }

    /**
     * Edit article content.
     * @param requester user who requested
     * @param article article
     * @param content new content
     * @return Returns the edited article
     */
    public Article editContent(User requester, Article article, String content) {
        if (article.isNotAuthor(requester)) {
            throw new IllegalArgumentException("you can't edit articles written by others.");
        }

        article.setContent(content);
        return articleRepository.save(article);
    }

    /**
     * Delete article.
     * @param requester user who requested
     * @param article article
     */
    public void deleteArticle(User requester, Article article) {
        if (article.isNotAuthor(requester)) {
            throw new IllegalArgumentException("you can't delete articles written by others.");
        }

        articleTagRepository.deleteByArticle(article);
        articleCommentRepository.deleteByArticle(article);
        articleRepository.delete(article);
    }

    /**
     * Check if the requester has favorited the article.
     * @param requester user who requested
     * @param article article
     * @return Returns true if already favorited
     */
    public boolean isFavorited(User requester, Article article) {
        return articleFavoriteRepository.existsByUserAndArticle(requester, article);
    }

    /**
     * Favorite article.
     * @param requester user who requested
     * @param article article
     */
    public void favoriteArticle(User requester, Article article) {
        if (this.isFavorited(requester, article)) {
            throw new IllegalArgumentException("you already favorited this article.");
        }

        articleFavoriteRepository.save(new ArticleFavorite(requester, article));
    }

    /**
     * Unfavorite article.
     * @param requester user who requested
     * @param article article
     */
    public void unfavoriteArticle(User requester, Article article) {
        if (!this.isFavorited(requester, article)) {
            throw new IllegalArgumentException("you already unfavorited this article.");
        }

        articleFavoriteRepository.deleteByUserAndArticle(requester, article);
    }

    /**
     * Get total favorites of article.
     * @param article article
     * @return Returns total favorites
     */
    public int getTotalFavorites(Article article) {
        return articleFavoriteRepository.countByArticle(article);
    }
}
