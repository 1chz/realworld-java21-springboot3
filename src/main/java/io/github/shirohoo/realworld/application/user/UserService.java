package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.application.config.BearerTokenProvider;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;
import io.github.shirohoo.realworld.domain.user.UserVO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BearerTokenProvider bearerTokenProvider;

    @Transactional
    public User signUp(SignUpUserRequest request) {
        this.validateUsernameAndEmail(request);
        User newUser = request.toUser();
        newUser.password(passwordEncoder.encode(request.password()));
        return userRepository.save(newUser);
    }

    private void validateUsernameAndEmail(SignUpUserRequest request) {
        String username = request.username();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
        }

        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }
    }

    @Transactional(readOnly = true)
    public UserVO login(LoginUserRequest request) {
        return userRepository
                .findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .map(user -> {
                    String token = bearerTokenProvider.provide(user);
                    return new UserVO(user.token(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    @Transactional
    public UserVO update(User user, UpdateUserRequest request) {
        this.updateEmail(user, request);
        this.updatePassword(user, request);
        this.updateUsername(user, request);
        this.updateUserDetails(user, request);
        return new UserVO(user);
    }

    private void updateEmail(User user, UpdateUserRequest request) {
        String email = request.email();
        if (email != null && !email.equals(user.email()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }
        if (email != null && !email.isBlank()) {
            user.email(email);
        }
    }

    private void updatePassword(User user, UpdateUserRequest request) {
        String password = request.password();
        if (password != null && !password.isBlank()) {
            String encoded = passwordEncoder.encode(password);
            user.password(encoded);
        }
    }

    private void updateUsername(User user, UpdateUserRequest request) {
        String username = request.username();
        if (username != null && !username.equals(user.username()) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
        }
        if (username != null && !username.isBlank()) {
            user.username(username);
        }
    }

    private void updateUserDetails(User user, UpdateUserRequest request) {
        user.bio(request.bio());
        user.image(request.image());
    }
}
