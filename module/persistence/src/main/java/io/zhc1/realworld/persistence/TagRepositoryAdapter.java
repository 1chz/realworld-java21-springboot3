package io.zhc1.realworld.persistence;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.config.CacheName;
import io.zhc1.realworld.model.Tag;
import io.zhc1.realworld.model.TagRepository;

@Repository
@RequiredArgsConstructor
class TagRepositoryAdapter implements TagRepository {
    private final TagJpaRepository tagJpaRepository;

    @Override
    @Cacheable(value = CacheName.ALL_TAGS)
    public List<Tag> findAll() {
        return tagJpaRepository.findAll();
    }
}
