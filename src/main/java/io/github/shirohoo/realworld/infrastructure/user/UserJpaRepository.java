package io.github.shirohoo.realworld.infrastructure.user;

import io.github.shirohoo.realworld.domain.user.User;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
    boolean existsByUsernameAndEmail(String username, String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByGuid(UUID guid);
}
