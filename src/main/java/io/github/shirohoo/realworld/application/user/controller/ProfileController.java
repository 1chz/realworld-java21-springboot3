package io.github.shirohoo.realworld.application.user.controller;

import io.github.shirohoo.realworld.application.user.service.ProfileService;
import io.github.shirohoo.realworld.domain.user.ProfileVO;
import io.github.shirohoo.realworld.domain.user.User;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public ProfileResponse getProfile(User me, @PathVariable("username") String target) {
        ProfileVO profile = profileService.getProfile(me, target);
        return new ProfileResponse(profile);
    }

    @PostMapping("/api/profiles/{username}/follow")
    public ProfileResponse follow(User me, @PathVariable("username") String target) {
        ProfileVO profile = profileService.follow(me, target);
        return new ProfileResponse(profile);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    public ProfileResponse unfollow(User me, @PathVariable("username") String target) {
        ProfileVO profile = profileService.unfollow(me, target);
        return new ProfileResponse(profile);
    }
}
