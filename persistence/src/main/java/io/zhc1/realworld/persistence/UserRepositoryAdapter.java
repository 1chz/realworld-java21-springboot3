package io.zhc1.realworld.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.PasswordEncoder;
import io.zhc1.realworld.core.model.User;
import io.zhc1.realworld.core.model.UserRepository;

@Repository
@RequiredArgsConstructor
class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsBy(String email, String username) {
        return userJpaRepository.existsByEmailOrUsername(email, username);
    }

    @Override
    @Transactional
    public User updateUserDetails(
            UUID userId,
            PasswordEncoder passwordEncoder,
            String email,
            String username,
            String password,
            String bio,
            String imageUrl) {
        return this.findById(userId)
                .map(user -> {
                    if (!user.equalsEmail(email) && this.existsByEmail(email)) {
                        throw new IllegalArgumentException("email is already exists.");
                    }

                    if (!user.equalsUsername(username) && this.existsByUsername(username)) {
                        throw new IllegalArgumentException("username is already exists.");
                    }

                    user.setEmail(email);
                    user.setUsername(username);
                    user.encryptPassword(password, passwordEncoder);
                    user.setBio(bio);
                    user.setImageUrl(imageUrl);
                    return userJpaRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("user not found."));
    }
}
