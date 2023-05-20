package io.github.shirohoo.realworld.domain.article;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Set<Comment> findByArticleOrderByCreatedAtDesc(Article article);
}
