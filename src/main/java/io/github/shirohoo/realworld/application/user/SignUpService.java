package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class SignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(User newUser) {
        if (userRepository.existsByUsernameAndEmail(newUser.getUsername(), newUser.getEmail())) {
            throw new IllegalArgumentException("User already exists.");
        }

        newUser = newUser.encryptPasswords(passwordEncoder);
        userRepository.save(newUser);
    }
}
