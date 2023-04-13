package io.github.shirohoo.realworld.application.user;

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
    public ProfileVO getProfile(User me, String to) {
        return userRepository
                .findByUsername(to)
                .map(it -> User.retrievesProfile(me, it))
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(to)));
    }

    @Transactional(readOnly = true)
    public ProfileVO getProfile(User me, User to) {
        return User.retrievesProfile(me, to);
    }

    @Transactional
    public ProfileVO follow(User me, String to) {
        return userRepository
                .findByUsername(to)
                .map(me::follow)
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(to)));
    }

    @Transactional
    public ProfileVO follow(User me, User to) {
        return me.follow(to);
    }

    @Transactional
    public ProfileVO unfollow(User me, String to) {
        return userRepository
                .findByUsername(to)
                .map(me::unfollow)
                .orElseThrow(() -> new NoSuchElementException("User(`%s`) not found".formatted(to)));
    }

    @Transactional
    public ProfileVO unfollow(User me, User to) {
        return me.unfollow(to);
    }
}
