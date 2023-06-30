package io.shirohoo.realworld.application.user.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.application.user.service.ProfileService;
import io.shirohoo.realworld.domain.user.ProfileVO;
import io.shirohoo.realworld.domain.user.User;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public ProfileRecord getProfile(User me, @PathVariable("username") String target) {
        ProfileVO profile = profileService.getProfile(me, target);
        return new ProfileRecord(profile);
    }

    @PostMapping("/api/profiles/{username}/follow")
    public ProfileRecord follow(User me, @PathVariable("username") String target) {
        ProfileVO profile = profileService.follow(me, target);
        return new ProfileRecord(profile);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    public ProfileRecord unfollow(User me, @PathVariable("username") String target) {
        ProfileVO profile = profileService.unfollow(me, target);
        return new ProfileRecord(profile);
    }
}
