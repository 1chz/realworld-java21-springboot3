package io.github.shirohoo.realworld.application.user;

import static org.springframework.http.HttpStatus.CREATED;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.Users;

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

@RestController
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping("/api/users")
    public ModelAndView signUp(@RequestBody UserSignUpRequest request, HttpServletRequest httpServletRequest) {
        userService.signUp(request);

        UserLoginRequest loginRequest = new UserLoginRequest(request.email(), request.password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", loginRequest);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/users/login")
    public UserResponse login(@RequestBody UserLoginRequest request) {
        Users users = userService.login(request);
        return new UserResponse(users);
    }

    @GetMapping("/api/user")
    public UserResponse getCurrentUser(User user) {
        Users users = new Users(user);
        return new UserResponse(users);
    }

    @PutMapping("/api/user")
    public UserResponse updateCurrentUser(User user, @RequestBody UserUpdateRequest request) {
        Users users = userService.update(user, request);
        return new UserResponse(users);
    }
}
