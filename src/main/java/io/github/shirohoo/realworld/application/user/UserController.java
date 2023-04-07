package io.github.shirohoo.realworld.application.user;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@RestController
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/users")
    public ModelAndView signUp(@RequestBody UserRegistrationRequest request, HttpServletRequest httpServletRequest) {
        userService.signUp(request);

        UserLoginRequest loginRequest = new UserLoginRequest(request.email(), request.password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", loginRequest);
    }

    @PostMapping("/api/users/login")
    public UserResponse login(@RequestBody UserLoginRequest request) {
        return userService.login(request);
    }
}
