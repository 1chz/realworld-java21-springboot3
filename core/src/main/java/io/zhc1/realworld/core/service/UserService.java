package io.zhc1.realworld.core.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.PasswordEncoder;
import io.zhc1.realworld.core.model.User;
import io.zhc1.realworld.core.model.UserRegistry;
import io.zhc1.realworld.core.model.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get user by id.
     *
     * @return Returns user
     */
    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("user not found."));
    }

    /**
     * Get user by username.
     *
     * @return Returns user
     */
    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("user not found."));
    }

    /**
     * Register a new user in the system.
     *
     * @param registry User registration information
     * @return Returns the registered user
     */
    @SuppressWarnings("UnusedReturnValue")
    public User signup(UserRegistry registry) {
        if (userRepository.existsBy(registry.email(), registry.username())) {
            throw new IllegalArgumentException("email or username is already exists.");
        }

        var requester = new User(registry);
        requester.encryptPassword(registry.password(), passwordEncoder);

        return userRepository.save(requester);
    }

    /**
     * Login to the system.
     *
     * @param email users email
     * @param password users password
     * @return Returns the logged-in user
     */
    public User login(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is required.");
        }

        return userRepository
                .findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("invalid email or password."));
    }

    /**
     * Update user information.
     * @param userId The user who requested the update
     * @param email users email
     * @param username users username
     * @param password users password
     * @param bio users bio
     * @param imageUrl users imageUrl
     * @return Returns the updated user
     */
    public User updateUserDetails(
            UUID userId, String email, String username, String password, String bio, String imageUrl) {
        if (userId == null) {
            throw new IllegalArgumentException("user id is required.");
        }

        return userRepository.updateUserDetails(userId, passwordEncoder, email, username, password, bio, imageUrl);
    }
}
