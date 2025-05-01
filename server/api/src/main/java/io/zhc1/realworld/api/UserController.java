package io.zhc1.realworld.api;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.request.LoginUserRequest;
import io.zhc1.realworld.api.request.SignupRequest;
import io.zhc1.realworld.api.request.UpdateUserRequest;
import io.zhc1.realworld.api.response.UsersResponse;
import io.zhc1.realworld.config.AuthToken;
import io.zhc1.realworld.config.AuthTokenProvider;
import io.zhc1.realworld.model.User;
import io.zhc1.realworld.model.UserRegistry;
import io.zhc1.realworld.service.UserService;

@RestController
@RequiredArgsConstructor
class UserController {
    private static final String LOGIN_URL = "/api/users/login";

    private final UserService userService;
    private final AuthTokenProvider bearerTokenProvider;

    @PostMapping("/api/users")
    public ModelAndView signup(HttpServletRequest httpServletRequest, @RequestBody SignupRequest request) {
        var userRegistry = new UserRegistry(
                request.user().email(),
                request.user().username(),
                request.user().password());

        userService.signup(userRegistry);

        // Redirect to login API to automatically login when signup is complete
        var loginRequest =
                new LoginUserRequest(request.user().email(), request.user().password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);

        return new ModelAndView("redirect:" + LOGIN_URL, "user", Map.of("user", loginRequest));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(LOGIN_URL)
    public UsersResponse login(@RequestBody LoginUserRequest request) {
        var email = request.user().email();
        var password = request.user().password();

        var user = userService.login(email, password);
        var authToken = bearerTokenProvider.createAuthToken(user);

        return UsersResponse.from(user, authToken);
    }

    @GetMapping("/api/user")
    public UsersResponse getUser(AuthToken actorsToken) {
        var actor = userService.getUser(actorsToken.userId());

        return UsersResponse.from(actor, actorsToken.tokenValue());
    }

    @PutMapping("/api/user")
    public UsersResponse updateUser(AuthToken actorsToken, @RequestBody UpdateUserRequest request) {
        User actor = userService.updateUserDetails(
                actorsToken.userId(),
                request.user().email(),
                request.user().username(),
                request.user().password(),
                request.user().bio(),
                request.user().image());

        return UsersResponse.from(actor, actorsToken.tokenValue());
    }
}
