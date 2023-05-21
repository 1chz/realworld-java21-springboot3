package io.github.shirohoo.realworld.application.user.service;

import io.github.shirohoo.realworld.domain.user.ProfileVO;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileVO getProfile(User me, String targetUsername) {
        return this.getProfile(
                me,
                userRepository
                        .findByUsername(targetUsername)
                        .orElseThrow(
                                () -> new NoSuchElementException("User(`%s`) not found".formatted(targetUsername))));
    }

    @Transactional(readOnly = true)
    public ProfileVO getProfile(User me, User target) {
        return new ProfileVO(me, target);
    }

    @Transactional
    public ProfileVO follow(User me, String targetUsername) {
        return this.follow(
                me,
                userRepository
                        .findByUsername(targetUsername)
                        .orElseThrow(
                                () -> new NoSuchElementException("User(`%s`) not found".formatted(targetUsername))));
    }

    @Transactional
    public ProfileVO follow(User me, User target) {
        return me.follow(target);
    }

    @Transactional
    public ProfileVO unfollow(User me, String targetUsername) {
        return this.unfollow(
                me,
                userRepository
                        .findByUsername(targetUsername)
                        .orElseThrow(
                                () -> new NoSuchElementException("User(`%s`) not found".formatted(targetUsername))));
    }

    @Transactional
    public ProfileVO unfollow(User me, User target) {
        return me.unfollow(target);
    }
}
