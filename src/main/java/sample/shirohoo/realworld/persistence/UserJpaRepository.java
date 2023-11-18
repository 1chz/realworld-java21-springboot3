package sample.shirohoo.realworld.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.User;

interface UserJpaRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmailOrUsername(String email, String username);
}
