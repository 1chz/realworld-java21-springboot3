package io.github.shirohoo.realworld.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record UserVO(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String password,
        String bio,
        String image,
        String token) {}
