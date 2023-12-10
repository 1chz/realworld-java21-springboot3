package sample.shirohoo.realworld.core.model;

import java.util.List;
import java.util.Optional;

public interface ArticleCommentRepository {
  ArticleComment save(ArticleComment articleComment);

  Optional<ArticleComment> findById(int commentId);

  List<ArticleComment> findByArticleOrderByCreatedAtDesc(Article article);

  void delete(ArticleComment articleComment);

  void deleteByArticle(Article article);
}
