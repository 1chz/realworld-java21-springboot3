package io.zhc1.realworld.core.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.SocialRepository;
import io.zhc1.realworld.core.model.User;
import io.zhc1.realworld.core.model.UserFollow;

@Service
@RequiredArgsConstructor
public class SocialService {
    private final SocialRepository socialRepository;

    /**
     * Check if the follower is following the following.
     *
     * @return Returns true if already following
     */
    public boolean isFollowing(User follower, User following) {
        return socialRepository.existsBy(follower, following);
    }

    /** Follow user. */
    public void follow(User follower, User following) {
        if (this.isFollowing(follower, following)) {
            return;
        }

        socialRepository.save(new UserFollow(follower, following));
    }

    /** Unfollow user. */
    public void unfollow(User follower, User following) {
        if (this.isFollowing(follower, following)) {
            socialRepository.deleteBy(follower, following);
        }
    }
}
