package io.github.shirohoo.realworld.application.user;

import static org.springframework.http.HttpStatus.CREATED;

import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public ProfileDTO getProfile(User me, @PathVariable("username") String to) {
        Profile profile = profileService.getProfile(me, to);
        return new ProfileDTO(profile);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/profiles/{username}/follow")
    public ProfileDTO follow(User me, @PathVariable("username") String to) {
        Profile profile = profileService.follow(me, to);
        return new ProfileDTO(profile);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    public ProfileDTO unfollow(User me, @PathVariable("username") String to) {
        Profile profile = profileService.unfollow(me, to);
        return new ProfileDTO(profile);
    }
}
