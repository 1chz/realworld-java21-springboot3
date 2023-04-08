package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.application.config.TokenProvider;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

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
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .map(user -> {
                    String token = tokenProvider.provide(user);
                    return new UserResponse(user.token(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    @Transactional
    public User update(User user, UserUpdateRequest request) {
        String email = request.email();
        if (email != null && !email.equals(user.email()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email(`%s`) already exists.".formatted(email));
        }

        String password = request.password();
        if (password != null) {
            String encoded = passwordEncoder.encode(password);
            user.password(encoded);
        }

        String username = request.username();
        if (username != null && !username.equals(user.username()) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username(`%s`) already exists.".formatted(username));
        }

        user.email(email).username(username).bio(request.bio()).image(request.image());

        return userRepository.save(user);
    }
}
