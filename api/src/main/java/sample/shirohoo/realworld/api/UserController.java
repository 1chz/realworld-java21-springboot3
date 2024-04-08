package sample.shirohoo.realworld.api;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.request.LoginUserRequest;
import sample.shirohoo.realworld.api.request.SignupRequest;
import sample.shirohoo.realworld.api.request.UpdateUserRequest;
import sample.shirohoo.realworld.api.response.UsersResponse;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserRegistry;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class UserController {
    private static final String LOGIN_URL = "/api/users/login";

    private final UserService userService;
    private final RealWorldBearerTokenProvider bearerTokenProvider;

    @PostMapping("/api/users")
    public ModelAndView doPost(HttpServletRequest httpServletRequest, @RequestBody SignupRequest request) {
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
    public UsersResponse doPost(@RequestBody LoginUserRequest request) {
        var email = request.user().email();
        var password = request.user().password();

        var user = userService.login(email, password);
        var accessToken = bearerTokenProvider.getToken(user);

        return UsersResponse.from(user, accessToken.getTokenValue());
    }

    @GetMapping("/api/user")
    public UsersResponse doGet(JwtAuthenticationToken authentication) {
        var user = userService.getUser(UUID.fromString(authentication.getName()));

        return UsersResponse.from(user, authentication.getToken().getTokenValue());
    }

    @PutMapping("/api/user")
    public UsersResponse doPut(JwtAuthenticationToken authentication, @RequestBody UpdateUserRequest request) {
        User requester = userService.updateUserDetails(
                UUID.fromString(authentication.getName()),
                request.user().email(),
                request.user().username(),
                request.user().password(),
                request.user().bio(),
                request.user().image());

        return UsersResponse.from(requester, authentication.getToken().getTokenValue());
    }
}
