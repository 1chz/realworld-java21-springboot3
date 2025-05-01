package io.zhc1.realworld.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.model.User;
import io.zhc1.realworld.model.UserFollow;
import io.zhc1.realworld.model.UserRelationshipRepository;

@Service
@RequiredArgsConstructor
public class UserRelationshipService {
    private final UserRelationshipRepository userRelationshipRepository;

    /**
     * Check if the follower is following the following.
     *
     * @return Returns true if already following
     */
    public boolean isFollowing(User follower, User following) {
        return userRelationshipRepository.existsBy(follower, following);
    }

    /** Follow user. */
    public void follow(User follower, User following) {
        if (this.isFollowing(follower, following)) {
            return;
        }

        userRelationshipRepository.save(new UserFollow(follower, following));
    }

    /** Unfollow user. */
    public void unfollow(User follower, User following) {
        if (this.isFollowing(follower, following)) {
            userRelationshipRepository.deleteBy(follower, following);
        }
    }
}
