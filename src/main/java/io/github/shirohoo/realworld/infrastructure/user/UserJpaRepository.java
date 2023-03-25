package io.github.shirohoo.realworld.infrastructure.user;

import io.github.shirohoo.realworld.domain.user.User;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByGuid(UUID guid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndEmail(String username, String email);
}
