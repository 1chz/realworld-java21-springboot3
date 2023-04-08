package io.github.shirohoo.realworld.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, FollowingPK> {
    boolean existsByMeAndFollowing(User me, User following);
}
