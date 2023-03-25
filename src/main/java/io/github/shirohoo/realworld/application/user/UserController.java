package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;

import java.security.Principal;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("/api/user")
    public User user(Principal principal) {
        String guid = principal.getName();
        String token = ((JwtAuthenticationToken) principal).getToken().getTokenValue();
        return userService.getUser(token, guid);
    }
}
