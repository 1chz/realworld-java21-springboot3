package io.github.shirohoo.realworld.application.profile;

import io.github.shirohoo.realworld.domain.follow.Follow;
import io.github.shirohoo.realworld.domain.follow.FollowId;
import io.github.shirohoo.realworld.domain.follow.FollowRepository;
import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ProfileService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public Profile getProfile(String targetUsername) {
        return userRepository
                .findByUsername(targetUsername)
                .map(u -> new Profile(u, false))
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(targetUsername)));
    }

    @Transactional(readOnly = true)
    public Profile getProfile(UUID currentUserGuid, String targetUsername) {
        User currentUser = userRepository
                .findByGuid(currentUserGuid)
                .orElseThrow(
                        () -> new NoSuchElementException("Invalid follower guid(`%s`)".formatted(currentUserGuid)));

        User targetUser = userRepository
                .findByUsername(targetUsername)
                .orElseThrow(() -> new NoSuchElementException("Followee(`%s`) not found".formatted(targetUsername)));

        FollowId followId = new FollowId(currentUser.getId(), targetUser.getId());
        if (followRepository.existsById(followId)) {
            return new Profile(targetUser, true);
        }
        return new Profile(targetUser, false);
    }

    @Transactional
    public Profile follow(UUID followerGuid, String followeeName) {
        User follower = userRepository
                .findByGuid(followerGuid)
                .orElseThrow(() -> new NoSuchElementException("Invalid follower guid(`%s`)".formatted(followerGuid)));

        User followee = userRepository
                .findByUsername(followeeName)
                .orElseThrow(() -> new NoSuchElementException("Followee(`%s`) not found".formatted(followeeName)));

        FollowId followId = new FollowId(follower.getId(), followee.getId());
        Follow follow = new Follow(followId);
        followRepository.save(follow);

        return new Profile(followee, true);
    }

    @Transactional
    public Profile unfollow(UUID followerGuid, String followeeName) {
        User follower = userRepository
                .findByGuid(followerGuid)
                .orElseThrow(() -> new NoSuchElementException("Invalid follower guid(`%s`)".formatted(followerGuid)));

        User followee = userRepository
                .findByUsername(followeeName)
                .orElseThrow(() -> new NoSuchElementException("Followee(`%s`) not found".formatted(followeeName)));

        followRepository
                .findById(new FollowId(follower.getId(), followee.getId()))
                .ifPresent(followRepository::delete);

        return new Profile(followee, false);
    }
}
