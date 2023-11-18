package sample.shirohoo.realworld.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserFollow;

interface UserFollowJpaRepository extends JpaRepository<UserFollow, Integer> {
    List<UserFollow> findByFollower(User follower);

    void deleteByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);
}
