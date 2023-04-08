package io.github.shirohoo.realworld.application.user;

import static org.springframework.http.HttpStatus.CREATED;

import io.github.shirohoo.realworld.domain.user.User;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProfileController {
    private final ProfileService profileService;

    ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/profiles/{username}/follow")
    public ProfileResponse follow(User user, @PathVariable String username) {
        return profileService.follow(user, username);
    }
}
