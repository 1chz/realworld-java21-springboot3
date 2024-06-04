package io.zhc1.realworld.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.Tag;
import io.zhc1.realworld.core.model.TagRepository;

@Repository
@RequiredArgsConstructor
class TagRepositoryAdapter implements TagRepository {
    private final TagJpaRepository tagJpaRepository;

    @Override
    public List<Tag> findAll() {
        return tagJpaRepository.findAll();
    }
}
