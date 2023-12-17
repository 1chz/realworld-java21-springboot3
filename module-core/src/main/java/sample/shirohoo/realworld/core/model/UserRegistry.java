package sample.shirohoo.realworld.core.model;

/**
 * User registration information.
 *
 * @param email user email
 * @param username user name
 * @param password user password
 */
public record UserRegistry(String email, String username, String password) {
    public UserRegistry {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be null or blank.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password must not be null or blank.");
        }
    }
}
