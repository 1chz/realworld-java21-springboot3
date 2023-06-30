package io.shirohoo.realworld.application.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.application.config.BearerTokenProvider;
import io.shirohoo.realworld.application.user.controller.LoginRequest;
import io.shirohoo.realworld.application.user.controller.SignUpRequest;
import io.shirohoo.realworld.application.user.controller.UpdateUserRequest;
import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserRepository;
import io.shirohoo.realworld.domain.user.UserVO;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BearerTokenProvider bearerTokenProvider;

    @Transactional
    public User signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email `%s` is already exists.".formatted(request.email()));
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username `%s` is already exists.".formatted(request.username()));
        }

        User newUser = this.createNewUser(request);
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public UserVO login(LoginRequest request) {
        return userRepository
                .findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> {
                    String token = bearerTokenProvider.createBearerToken(user);
                    return new UserVO(user.setToken(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    @Transactional
    public UserVO update(User user, UpdateUserRequest request) {
        String email = request.email();
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email `%s` is already exists.".formatted(email));
        }

        String username = request.username();
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username `%s` is already exists.".formatted(request.username()));
        }

        user.updateEmail(email);
        user.updateUsername(username);
        user.updatePassword(passwordEncoder, request.password());
        user.updateBio(request.bio());
        user.updateImage(request.image());

        return new UserVO(user);
    }

    private User createNewUser(SignUpRequest request) {
        return User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }
}
