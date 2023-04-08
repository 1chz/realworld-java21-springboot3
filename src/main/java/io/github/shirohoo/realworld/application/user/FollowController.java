package io.github.shirohoo.realworld.application.user;

import static org.springframework.http.HttpStatus.CREATED;

import io.github.shirohoo.realworld.domain.user.User;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class FollowController {
    private final FollowService followService;

    FollowController(FollowService followService) {
        this.followService = followService;
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/profiles/{username}/follow")
    public ProfileResponse follow(User user, @PathVariable String username) {
        return followService.follow(user, username);
    }
}
