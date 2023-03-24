package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(User newUser) {
        if (userRepository.existsByUsernameAndEmail(newUser.getUsername(), newUser.getEmail())) {
            throw new IllegalArgumentException("User already exists.");
        }

        newUser = newUser.encryptPasswords(passwordEncoder);
        userRepository.save(newUser);
    }
}
