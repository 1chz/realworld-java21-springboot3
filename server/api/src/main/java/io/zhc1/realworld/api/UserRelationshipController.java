package io.zhc1.realworld.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.response.ProfilesResponse;
import io.zhc1.realworld.config.AuthToken;
import io.zhc1.realworld.mixin.AuthenticationAwareMixin;
import io.zhc1.realworld.service.UserRelationshipService;
import io.zhc1.realworld.service.UserService;

@RestController
@RequiredArgsConstructor
class UserRelationshipController implements AuthenticationAwareMixin {
    private final UserService userService;
    private final UserRelationshipService userRelationshipService;

    @GetMapping("/api/profiles/{username}")
    ProfilesResponse getUserProfile(AuthToken profileViewersToken, @PathVariable("username") String targetUsername) {
        var targetUser = userService.getUser(targetUsername);

        if (this.isAnonymousUser(profileViewersToken)) {
            return ProfilesResponse.from(targetUser);
        }

        var viewer = userService.getUser(profileViewersToken.userId());
        var isFollowing = userRelationshipService.isFollowing(viewer, targetUser);

        return ProfilesResponse.from(targetUser, isFollowing);
    }

    @PostMapping("/api/profiles/{username}/follow")
    ProfilesResponse follow(AuthToken followersToken, @PathVariable("username") String targetUsername) {
        var follower = userService.getUser(followersToken.userId());
        var following = userService.getUser(targetUsername);

        userRelationshipService.follow(follower, following);

        return ProfilesResponse.from(following, true);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    ProfilesResponse unfollow(AuthToken followersToken, @PathVariable("username") String targetUsername) {
        var follower = userService.getUser(followersToken.userId());
        var following = userService.getUser(targetUsername);

        userRelationshipService.unfollow(follower, following);

        return ProfilesResponse.from(following, false);
    }
}
