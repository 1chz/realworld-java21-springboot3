package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.application.config.TokenProvider;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public User signUp(UserRegistrationRequest request) {
        String username = request.username();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username(`%s`) already exists.".formatted(username));
        }

        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email(`%s`) already exists.".formatted(email));
        }

        String encoded = passwordEncoder.encode(request.password());
        request = request.encryptPasswords(encoded);
        return userRepository.save(request.toUser());
    }

    @Transactional(readOnly = true)
    public UserResponse login(UserLoginRequest request) {
        return userRepository
                .findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> {
                    String token = tokenProvider.provide(user);
                    return new UserResponse(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage());
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }
}
