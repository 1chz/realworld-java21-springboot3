package sample.shirohoo.realworld.core.model;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmailOrUsername(String email, String username);
}
