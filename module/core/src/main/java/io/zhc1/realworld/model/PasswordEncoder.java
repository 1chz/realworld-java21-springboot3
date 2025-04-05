package io.zhc1.realworld.model;

/** Can verify that the user's password is correct or encrypt the password. */
public interface PasswordEncoder {
    boolean matches(String rawPassword, String encodedPassword);

    String encode(String rawPassword);
}
