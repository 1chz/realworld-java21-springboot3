package io.github.shirohoo.realworld.application.profile;

import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ProfileService {
    private final UserRepository userRepository;

    ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Profile getProfile(User me, String targetUsername) {
        return userRepository
                .findByProfileUsername(targetUsername)
                .map(target -> target.getProfile(me))
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(targetUsername)));
    }

    @Transactional
    public Profile follow(User me, String targetUsername) {
        return userRepository
                .findByProfileUsername(targetUsername)
                .map(me::follow)
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(targetUsername)));
    }

    @Transactional
    public Profile unfollow(User me, String targetUsername) {
        return userRepository
                .findByProfileUsername(targetUsername)
                .map(me::unfollow)
                .orElseThrow(() -> new NoSuchElementException("Following(`%s`) not found".formatted(targetUsername)));
    }
}
