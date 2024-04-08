package sample.shirohoo.realworld.api;

import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.response.ProfilesResponse;
import sample.shirohoo.realworld.core.service.SocialService;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class SocialController {
    private final UserService userService;
    private final SocialService socialService;

    @GetMapping("/api/profiles/{username}")
    ProfilesResponse doGet(Authentication authentication, @PathVariable("username") String targetUsername) {
        var targetUser = userService.getUser(targetUsername);

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ProfilesResponse.from(targetUser);
        }

        var me = userService.getUser(UUID.fromString(authentication.getName()));
        var isFollowing = socialService.isFollowing(me, targetUser);

        return ProfilesResponse.from(targetUser, isFollowing);
    }

    @PostMapping("/api/profiles/{username}/follow")
    ProfilesResponse doPost(Authentication authentication, @PathVariable("username") String targetUsername) {
        var follower = userService.getUser(UUID.fromString(authentication.getName()));
        var following = userService.getUser(targetUsername);

        socialService.follow(follower, following);

        return ProfilesResponse.from(following, true);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    ProfilesResponse doDelete(Authentication authentication, @PathVariable("username") String targetUsername) {
        var follower = userService.getUser(UUID.fromString(authentication.getName()));
        var following = userService.getUser(targetUsername);

        socialService.unfollow(follower, following);

        return ProfilesResponse.from(following, false);
    }
}
