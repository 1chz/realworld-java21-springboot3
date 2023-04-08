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
    public User signUp(UserSignUpRequest request) {
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
                    return new UserResponse(user.bindToken(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    @Transactional
    public User update(User user, UserUpdateRequest request) {
        String email = request.email();
        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email(`%s`) already exists.".formatted(email));
        }

        String password = request.password();
        if (password != null) {
            String encoded = passwordEncoder.encode(password);
            user.setPassword(encoded);
        }

        String username = request.username();
        if (username != null && !username.equals(user.getUsername()) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username(`%s`) already exists.".formatted(username));
        }
        user.setEmail(email);
        user.setUsername(username);
        user.setBio(request.bio());
        user.setImage(request.image());

        return userRepository.save(user);
    }
}
