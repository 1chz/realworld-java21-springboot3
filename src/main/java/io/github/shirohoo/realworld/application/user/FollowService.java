package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.FollowerRepository;
import io.github.shirohoo.realworld.domain.user.FollowingRepository;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class FollowService {
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;

    public FollowService(
            UserRepository userRepository,
            FollowerRepository followerRepository,
            FollowingRepository followingRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.followingRepository = followingRepository;
    }

    @Transactional
    public ProfileResponse getProfile(User me, User target) {
        String username = target.getUsername();
        String bio = target.getBio();
        String image = target.getImage();
        boolean following = followingRepository.existsByMeAndFollowing(me, target);
        return new ProfileResponse(username, bio, image, following);
    }

    @Transactional
    public ProfileResponse follow(User me, String to) {
        return userRepository
                .findByUsername(to)
                .map(following -> this.follow(me, following))
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Transactional
    public ProfileResponse follow(User me, User to) {
        if (followingRepository.existsByMeAndFollowing(me, to)) {
            throw new IllegalStateException("Already following");
        }

        followingRepository.save(me.follow(to));

        return this.getProfile(me, to);
    }
}
