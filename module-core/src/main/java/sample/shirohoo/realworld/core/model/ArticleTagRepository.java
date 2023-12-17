package sample.shirohoo.realworld.core.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ArticleTagRepository {
    @SuppressWarnings("UnusedReturnValue")
    List<ArticleTag> saveAll(Collection<ArticleTag> articleTags);

    Set<ArticleTag> findByArticleAndTagIn(Article article, Collection<Tag> tags);
}
