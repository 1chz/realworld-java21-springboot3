package io.zhc1.realworld.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.response.ProfilesResponse;
import io.zhc1.realworld.config.RealWorldAuthenticationToken;
import io.zhc1.realworld.service.SocialService;
import io.zhc1.realworld.service.UserService;

@RestController
@RequiredArgsConstructor
class SocialController {
    private final UserService userService;
    private final SocialService socialService;

    @GetMapping("/api/profiles/{username}")
    ProfilesResponse getUserProfile(
            RealWorldAuthenticationToken profileViewersToken, @PathVariable("username") String targetUsername) {
        var targetUser = userService.getUser(targetUsername);

        boolean isAnonymousViewer = profileViewersToken == null || !profileViewersToken.isAuthenticated();
        if (isAnonymousViewer) {
            return ProfilesResponse.from(targetUser);
        }

        var viewer = userService.getUser(profileViewersToken.userId());
        var isFollowing = socialService.isFollowing(viewer, targetUser);

        return ProfilesResponse.from(targetUser, isFollowing);
    }

    @PostMapping("/api/profiles/{username}/follow")
    ProfilesResponse follow(
            RealWorldAuthenticationToken followersToken, @PathVariable("username") String targetUsername) {
        var follower = userService.getUser(followersToken.userId());
        var following = userService.getUser(targetUsername);

        socialService.follow(follower, following);

        return ProfilesResponse.from(following, true);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    ProfilesResponse unfollow(
            RealWorldAuthenticationToken followersToken, @PathVariable("username") String targetUsername) {
        var follower = userService.getUser(followersToken.userId());
        var following = userService.getUser(targetUsername);

        socialService.unfollow(follower, following);

        return ProfilesResponse.from(following, false);
    }
}
