package io.zhc1.realworld.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import io.zhc1.realworld.model.PasswordEncoder;

/* Note: Connects the PasswordEncoder of 'core' module and 'Spring Security'  */
@Component
class SecurityPasswordEncoderAdapter implements PasswordEncoder {
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    SecurityPasswordEncoderAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
