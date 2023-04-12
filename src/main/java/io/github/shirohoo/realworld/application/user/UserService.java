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
        String username = request.username();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
        }

        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }

        String encoded = passwordEncoder.encode(request.password());
        request = request.encryptPasswords(encoded);
        return userRepository.save(request.toUser());
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
        String email = request.email();
        if (email != null && !email.equals(user.email()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }
        if (email != null && !email.isBlank()) user.email(email);

        String password = request.password();
        if (password != null && !password.isBlank()) {
            String encoded = passwordEncoder.encode(password);
            user.password(encoded);
        }

        String username = request.username();
        if (username != null && !username.equals(user.username()) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
        }
        if (username != null && !username.isBlank()) user.username(username);

        user.bio(request.bio());
        user.image(request.image());

        return new UserVO(userRepository.save(user));
    }
}
