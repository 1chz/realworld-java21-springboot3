package io.github.shirohoo.realworld.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    boolean existsByUsernameAndEmail(String username, String email);

    Optional<User> findByEmail(String email);
}
