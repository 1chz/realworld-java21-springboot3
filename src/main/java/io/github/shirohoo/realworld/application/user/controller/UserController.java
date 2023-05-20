package io.github.shirohoo.realworld.application.user.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.github.shirohoo.realworld.application.user.service.UserService;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserVO;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/users")
    public ModelAndView signUp(@RequestBody SignUpUserRequest request, HttpServletRequest httpServletRequest) {
        userService.signUp(request);

        // Redirect to login API to automatically login when signup is complete
        LoginUserRequest loginRequest = new LoginUserRequest(request.email(), request.password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", Map.of("user", loginRequest));
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/users/login")
    public UserResponse login(@RequestBody LoginUserRequest request) {
        UserVO userVO = userService.login(request);
        return new UserResponse(userVO);
    }

    @GetMapping("/api/user")
    public UserResponse getCurrentUser(User me) {
        UserVO userVO = new UserVO(me);
        return new UserResponse(userVO);
    }

    @PutMapping("/api/user")
    public UserResponse updateCurrentUser(User me, @RequestBody UpdateUserRequest request) {
        UserVO userVO = userService.update(me, request);
        return new UserResponse(userVO);
    }
}
