package io.zhc1.realworld.persistence;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.zhc1.realworld.core.model.Article;
import io.zhc1.realworld.core.model.User;

interface ArticleJpaRepository extends JpaRepository<Article, Integer>, JpaSpecificationExecutor<Article> {
    Optional<Article> findBySlug(String slug);

    @SuppressWarnings("NullableProblems")
    Page<Article> findAll(Specification<Article> spec, Pageable pageable);

    Page<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, Pageable pageable);

    boolean existsByTitle(String title);
}
