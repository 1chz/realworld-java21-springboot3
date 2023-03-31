package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;
import io.github.shirohoo.realworld.domain.user.UserVO;

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
    public User signUp(UserVO newUser) {
        String username = newUser.username();
        if (userRepository.existsByProfileUsername(username)) {
            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
        }

        String email = newUser.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }

        User user = User.from(newUser);
        user = user.encryptPasswords(passwordEncoder);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(String token, String guid) {
        return userRepository
                .findByGuid(UUID.fromString(guid))
                .map(user -> user.bind(token))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Transactional
    public User updateUser(User user, UserVO updateRequests) {
        String username = updateRequests.username();
        if (!user.sameUsername(username)) {
            if (username != null && userRepository.existsByProfileUsername(username)) {
                String msg = "Username(`%s`) already exists.".formatted(username);
                throw new IllegalArgumentException(msg);
            }
        }

        String email = updateRequests.email();
        if (!user.sameEmail(email)) {
            if (email != null && userRepository.existsByEmail(email)) {
                String msg = "Email(`%s`) already exists.".formatted(email);
                throw new IllegalArgumentException(msg);
            }
        }

        return user.updateUser(passwordEncoder, updateRequests);
    }
}
