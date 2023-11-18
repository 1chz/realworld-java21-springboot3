package sample.shirohoo.realworld.api;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
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
import sample.shirohoo.realworld.config.RealworldBearerTokenProvider;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserRegistry;
import sample.shirohoo.realworld.core.service.UserService;

@RestController
@RequiredArgsConstructor
class UserController {
    private static final String LOGIN_URL = "/api/users/login";

    private final UserService userService;
    private final RealworldBearerTokenProvider bearerTokenProvider;

    @PostMapping("/api/users")
    public ModelAndView doPost(HttpServletRequest httpServletRequest, @RequestBody SignupRequest request) {
        UserRegistry userRegistry = new UserRegistry(
                request.user().email(),
                request.user().username(),
                request.user().password());

        userService.signup(userRegistry);

        // Redirect to login API to automatically login when signup is complete
        LoginUserRequest loginUserRequest =
                new LoginUserRequest(request.user().email(), request.user().password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);

        return new ModelAndView("redirect:" + LOGIN_URL, "user", Map.of("user", loginUserRequest));
    }

    @ResponseStatus(CREATED)
    @PostMapping(LOGIN_URL)
    public UsersResponse doPost(@RequestBody LoginUserRequest request) {
        String email = request.user().email();
        String password = request.user().password();

        User user = userService.login(email, password);
        Jwt jwt = bearerTokenProvider.getToken(user);

        return UsersResponse.from(jwt.getTokenValue(), user);
    }

    @GetMapping("/api/user")
    public UsersResponse doGet(JwtAuthenticationToken authentication) {
        User user = userService.getUserById(UUID.fromString(authentication.getName()));

        return UsersResponse.from(authentication.getToken().getTokenValue(), user);
    }

    @PutMapping("/api/user")
    public UsersResponse doPut(JwtAuthenticationToken authentication, @RequestBody UpdateUserRequest request) {
        User requester = userService.getUserById(UUID.fromString(authentication.getName()));

        requester = userService.updateEmail(requester, request.user().email());
        requester = userService.updateUsername(requester, request.user().username());
        requester = userService.updatePassword(requester, request.user().password());
        requester = userService.updateBio(requester, request.user().bio());
        requester = userService.updateImageUrl(requester, request.user().image());

        return UsersResponse.from(authentication.getToken().getTokenValue(), requester);
    }
}
