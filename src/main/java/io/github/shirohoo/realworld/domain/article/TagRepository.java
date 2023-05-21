package io.github.shirohoo.realworld.domain.article;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    boolean existsByName(String name);

    Optional<Tag> findByName(String name);
}
