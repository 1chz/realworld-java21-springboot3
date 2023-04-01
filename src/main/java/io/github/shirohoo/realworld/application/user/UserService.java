package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;
import io.github.shirohoo.realworld.domain.user.UserVO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
            throw new IllegalArgumentException("username(`%s`) already exists.".formatted(username));
        }

        String email = newUser.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email(`%s`) already exists.".formatted(email));
        }

        String encoded = passwordEncoder.encode(newUser.password());
        newUser = newUser.encryptPasswords(encoded);
        return userRepository.save(User.from(newUser));
    }

    @Transactional
    public User updateUser(User user, UserVO updateRequests) {
        String username = updateRequests.username();
        if (!user.isSameUsername(username) && userRepository.existsByProfileUsername(username)) {
            throw new IllegalArgumentException("username(`%s`) already exists.".formatted(username));
        }

        String email = updateRequests.email();
        if (!user.isSameEmail(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email(`%s`) already exists.".formatted(email));
        }

        String password = updateRequests.password();
        if (StringUtils.hasText(password)) {
            String encoded = passwordEncoder.encode(password);
            updateRequests = updateRequests.encryptPasswords(encoded);
        }

        return user.update(updateRequests);
    }
}
