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
    public ProfileVO getProfile(User following, String followerName) {
        return userRepository
                .findByUsername(followerName)
                .map(follower -> follower.fetchProfileBy(following))
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(followerName)));
    }

    @Transactional(readOnly = true)
    public ProfileVO getProfile(User following, User follower) {
        return follower.fetchProfileBy(following);
    }

    @Transactional
    public ProfileVO follow(User me, String targetName) {
        return userRepository
                .findByUsername(targetName)
                .map(me::follow)
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(targetName)));
    }

    @Transactional
    public ProfileVO follow(User me, User target) {
        return me.follow(target);
    }

    @Transactional
    public ProfileVO unfollow(User me, String targetName) {
        return userRepository
                .findByUsername(targetName)
                .map(me::unfollow)
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(targetName)));
    }

    @Transactional
    public ProfileVO unfollow(User me, User target) {
        return me.unfollow(target);
    }
}
