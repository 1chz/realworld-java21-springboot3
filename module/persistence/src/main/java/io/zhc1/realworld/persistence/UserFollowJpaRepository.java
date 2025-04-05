package io.zhc1.realworld.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zhc1.realworld.model.User;
import io.zhc1.realworld.model.UserFollow;

interface UserFollowJpaRepository extends JpaRepository<UserFollow, Integer> {
    List<UserFollow> findByFollower(User follower);

    void deleteByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);
}
