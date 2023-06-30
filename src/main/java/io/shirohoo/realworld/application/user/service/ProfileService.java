package io.shirohoo.realworld.application.user.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.domain.user.ProfileVO;
import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileVO getProfile(User me, String targetUsername) {
        User targetUser = findUserByUsername(targetUsername);
        return new ProfileVO(me, targetUser);
    }

    @Transactional
    public ProfileVO follow(User me, String targetUsername) {
        User targetUser = findUserByUsername(targetUsername);
        return me.follow(targetUser);
    }

    @Transactional
    public ProfileVO unfollow(User me, String targetUsername) {
        User targetUser = findUserByUsername(targetUsername);
        return me.unfollow(targetUser);
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(username)));
    }
}
