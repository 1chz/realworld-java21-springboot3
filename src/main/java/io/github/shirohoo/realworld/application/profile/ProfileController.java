package io.github.shirohoo.realworld.application.profile;

import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProfileController {
    private final ProfileService profileService;

    ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/api/profiles/{username}")
    public Profile getProfile(User me, @PathVariable String username) {
        return profileService.getProfile(me, username);
    }

    @PostMapping("/api/profiles/{username}/follow")
    public Profile follow(User me, @PathVariable String username) {
        return profileService.follow(me, username);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    public Profile unfollow(User me, @PathVariable String username) {
        return profileService.unfollow(me, username);
    }
}
