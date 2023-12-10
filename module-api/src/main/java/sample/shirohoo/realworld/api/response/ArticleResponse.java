package sample.shirohoo.realworld.api.response;

import java.time.LocalDateTime;
import java.util.Collection;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleInfo;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.Tag;

public record ArticleResponse(
    String slug,
    String title,
    String description,
    String body,
    String[] tagList,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean favorited,
    int favoritesCount,
    ProfileResponse author) {
  public ArticleResponse(ArticleInfo articleInfo) {
    this(
        articleInfo.article(),
        articleInfo.articleTags(),
        articleInfo.favorited(),
        articleInfo.favoritesCount());
  }

  public ArticleResponse(
      Article article, Collection<ArticleTag> articleTags, boolean favorited, int favoritesCount) {
    this(
        article.getSlug(),
        article.getTitle(),
        article.getDescription(),
        article.getContent(),
        articleTags.stream().map(ArticleTag::getTag).map(Tag::getName).toArray(String[]::new),
        article.getCreatedAt(),
        article.getUpdatedAt(),
        favorited,
        favoritesCount,
        ProfileResponse.from(article.getAuthor(), false));
  }
}
