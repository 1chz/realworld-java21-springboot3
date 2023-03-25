package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUser(String token, String guid) {
        return userRepository
                .findByGuid(UUID.fromString(guid))
                .map(user -> user.withToken(token))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
