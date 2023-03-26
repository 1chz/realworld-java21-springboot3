package io.github.shirohoo.realworld.domain.user;

import java.util.Optional;

public interface FollowRepository {
    void save(Follow follow);

    Optional<Follow> findById(FollowId followId);

    boolean existsById(FollowId followId);

    void delete(Follow follow);
}
