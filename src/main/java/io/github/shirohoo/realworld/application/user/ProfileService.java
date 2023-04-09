package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ProfileService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Profile getProfile(User me, String to) {
        return userRepository
                .findByUsername(to)
                .map(it -> this.getProfile(me, it))
                .orElseThrow(userNotFound(to));
    }

    @Transactional(readOnly = true)
    public Profile getProfile(User me, User to) {
        return new Profile(me, to);
    }

    @Transactional
    public Profile follow(User me, String to) {
        return userRepository.findByUsername(to).map(it -> this.follow(me, it)).orElseThrow(userNotFound(to));
    }

    @Transactional
    public Profile follow(User me, User to) {
        me.follow(to);
        return this.getProfile(me, to);
    }

    @Transactional
    public Profile unfollow(User me, String to) {
        return userRepository
                .findByUsername(to)
                .map(it -> this.unfollow(me, it))
                .orElseThrow(userNotFound(to));
    }

    @Transactional
    public Profile unfollow(User me, User to) {
        me.unfollow(to);
        return this.getProfile(me, to);
    }

    private Supplier<NoSuchElementException> userNotFound(String username) {
        return () -> new NoSuchElementException("User(`%s`) not found".formatted(username));
    }
}
