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
        String message = "User(`%s`) not found".formatted(username);
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException(message));
    }
}
