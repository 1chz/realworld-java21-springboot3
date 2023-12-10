package sample.shirohoo.realworld.core.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.PasswordEncoder;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserRegistry;
import sample.shirohoo.realworld.core.model.UserRepository;

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
  public User getUserById(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("user not found."));
  }

  /**
   * Get user by username.
   *
   * @return Returns user
   */
  public User getUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("user not found."));
  }

  /**
   * Register a new user in the system.
   *
   * @param registry User registration information
   * @return Returns the registered user
   */
  @SuppressWarnings("UnusedReturnValue")
  public User signup(UserRegistry registry) {
    if (userRepository.existsByEmailOrUsername(registry.email(), registry.username())) {
      throw new IllegalArgumentException("email or username is already exists.");
    }

    User requester = new User(registry);
    requester.setPassword(passwordEncoder, registry.password());

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
   *
   * @param requester The user who requested the update
   * @param email users email
   * @return Returns the updated user
   */
  public User updateEmail(User requester, String email) {
    if (requester == null) {
      throw new IllegalArgumentException("requester is required.");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email is required.");
    }

    if (requester.equalsEmail(email)) {
      return requester;
    }
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("email is already exists.");
    }

    requester.setEmail(email);

    return userRepository.save(requester);
  }

  /**
   * Update user information.
   *
   * @param requester The user who requested the update
   * @param username users username
   * @return Returns the updated user
   */
  public User updateUsername(User requester, String username) {
    if (requester == null) {
      throw new IllegalArgumentException("requester is required.");
    }

    // Note: the E2E test defined by the RealWorld specification, username is nullable.
    // if (username == null || username.isBlank()) {
    //     throw new IllegalArgumentException("username is required.");
    // }

    if (requester.equalsUsername(username)) {
      return requester;
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("username is already exists.");
    }

    requester.setUsername(username);

    return userRepository.save(requester);
  }

  /**
   * Update user information.
   *
   * @param requester The user who requested the update
   * @param password users password
   * @return Returns the updated user
   */
  public User updatePassword(User requester, String password) {
    if (requester == null) {
      throw new IllegalArgumentException("requester is required.");
    }

    // Note:
    //  If necessary, you can check the password pattern here.
    //  the E2E test defined by the RealWorld specification, password is nullable.
    requester.setPassword(passwordEncoder, password);

    return userRepository.save(requester);
  }

  /**
   * Update user information.
   *
   * @param requester The user who requested the update
   * @param bio users bio
   * @return Returns the updated user
   */
  public User updateBio(User requester, String bio) {
    if (requester == null) {
      throw new IllegalArgumentException("requester is required.");
    }

    requester.setBio(bio);

    return userRepository.save(requester);
  }

  /**
   * Update user information.
   *
   * @param requester The user who requested the update
   * @param imageUrl users imageUrl
   * @return Returns the updated user
   */
  public User updateImageUrl(User requester, String imageUrl) {
    if (requester == null) {
      throw new IllegalArgumentException("requester is required.");
    }

    requester.setImageUrl(imageUrl);

    return userRepository.save(requester);
  }
}
