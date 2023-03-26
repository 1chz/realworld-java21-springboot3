package io.github.shirohoo.realworld.application.profile;

import io.github.shirohoo.realworld.domain.user.Profile;

import java.security.Principal;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public Profile getProfile(Principal principal, @PathVariable String username) {
        if (principal == null) {
            return profileService.getProfile(username);
        }
        UUID currentUserGuid = UUID.fromString(principal.getName());
        return profileService.getProfile(currentUserGuid, username);
    }

    @PostMapping("/api/profiles/{followeeName}/follow")
    public Profile follow(Principal principal, @PathVariable String followeeName) {
        UUID followerGuid = UUID.fromString(principal.getName());
        return profileService.follow(followerGuid, followeeName);
    }

    @DeleteMapping("/api/profiles/{followeeName}/follow")
    public Profile unfollow(Principal principal, @PathVariable String followeeName) {
        UUID followerGuid = UUID.fromString(principal.getName());
        return profileService.unfollow(followerGuid, followeeName);
    }
}
