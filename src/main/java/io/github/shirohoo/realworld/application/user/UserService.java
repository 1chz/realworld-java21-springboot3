package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(User newUser) {
        String username = newUser.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
        }

        String email = newUser.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }

        newUser = newUser.encryptPasswords(passwordEncoder);
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public User getUser(String token, String guid) {
        return userRepository
                .findByGuid(UUID.fromString(guid))
                .map(user -> user.withToken(token))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Transactional
    public User updateUser(String token, String guid, User updateRequests) {
        User user = userRepository
                .findByGuid(UUID.fromString(guid))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        String username = updateRequests.getUsername();
        if (!user.sameUsername(username)) {
            if (username != null && userRepository.existsByUsername(username)) {
                String msg = "Username(`%s`) already exists.".formatted(username);
                throw new IllegalArgumentException(msg);
            }
        }

        String email = updateRequests.getEmail();
        if (!user.sameEmail(email)) {
            if (email != null && userRepository.existsByEmail(email)) {
                String msg = "Email(`%s`) already exists.".formatted(email);
                throw new IllegalArgumentException(msg);
            }
        }

        return user.update(passwordEncoder, updateRequests).withToken(token);
    }
}
