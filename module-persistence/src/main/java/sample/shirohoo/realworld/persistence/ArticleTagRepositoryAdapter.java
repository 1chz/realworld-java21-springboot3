package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.ArticleTagRepository;
import sample.shirohoo.realworld.core.model.Tag;

@Repository
@RequiredArgsConstructor
class ArticleTagRepositoryAdapter implements ArticleTagRepository {
    private final ArticleTagJpaRepository articleTagJpaRepository;

    @Override
    public List<ArticleTag> saveAll(Collection<ArticleTag> articleTags) {
        return articleTagJpaRepository.saveAll(articleTags);
    }

    @Override
    public Set<ArticleTag> findByArticleAndTagIn(Article article, Collection<Tag> tags) {
        return articleTagJpaRepository.findByArticleAndTagIn(article, tags);
    }

    @Override
    @Transactional
    public void deleteByArticle(Article article) {
        articleTagJpaRepository.deleteByArticle(article);
    }
}
