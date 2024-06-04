package io.zhc1.realworld.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.SocialRepository;
import io.zhc1.realworld.core.model.User;
import io.zhc1.realworld.core.model.UserFollow;

@Repository
@RequiredArgsConstructor
class SocialRepositoryAdapter implements SocialRepository {
    private final UserFollowJpaRepository userFollowJpaRepository;

    @Override
    public void save(UserFollow userFollow) {
        userFollowJpaRepository.save(userFollow);
    }

    @Override
    public List<UserFollow> findByFollower(User follower) {
        return userFollowJpaRepository.findByFollower(follower);
    }

    @Override
    @Transactional
    public void deleteBy(User follower, User following) {
        userFollowJpaRepository.deleteByFollowerAndFollowing(follower, following);
    }

    @Override
    public boolean existsBy(User follower, User following) {
        return userFollowJpaRepository.existsByFollowerAndFollowing(follower, following);
    }
}
