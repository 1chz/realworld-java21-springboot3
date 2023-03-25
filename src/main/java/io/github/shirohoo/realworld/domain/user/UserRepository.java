package io.github.shirohoo.realworld.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    boolean existsByUsernameAndEmail(String username, String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByGuid(UUID guid);
}
