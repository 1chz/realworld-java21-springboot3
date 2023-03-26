package io.github.shirohoo.realworld.infrastructure.follow;

import io.github.shirohoo.realworld.domain.follow.Follow;
import io.github.shirohoo.realworld.domain.follow.FollowId;
import io.github.shirohoo.realworld.domain.follow.FollowRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {
    private final FollowJpaRepository followJpaRepository;

    @Override
    public void save(Follow follow) {
        followJpaRepository.save(follow);
    }

    @Override
    public Optional<Follow> findById(FollowId followId) {
        return followJpaRepository.findById(followId);
    }

    @Override
    public boolean existsById(FollowId followId) {
        return followJpaRepository.existsById(followId);
    }

    @Override
    public void delete(Follow follow) {
        followJpaRepository.delete(follow);
    }
}
