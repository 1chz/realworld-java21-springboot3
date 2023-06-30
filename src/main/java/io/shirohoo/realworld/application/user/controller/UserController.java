package io.shirohoo.realworld.application.user.controller;

import static org.springframework.http.HttpStatus.CREATED;

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

import io.shirohoo.realworld.application.user.service.UserService;
import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserVO;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/users")
    public ModelAndView signUp(@RequestBody SignUpRequest request, HttpServletRequest httpServletRequest) {
        userService.signUp(request);

        // Redirect to login API to automatically login when signup is complete
        LoginRequest loginRequest = new LoginRequest(request.email(), request.password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", Map.of("user", loginRequest));
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/users/login")
    public UserRecord login(@RequestBody LoginRequest request) {
        UserVO userVO = userService.login(request);
        return new UserRecord(userVO);
    }

    @GetMapping("/api/user")
    public UserRecord getCurrentUser(User me) {
        UserVO userVO = new UserVO(me);
        return new UserRecord(userVO);
    }

    @PutMapping("/api/user")
    public UserRecord updateCurrentUser(User me, @RequestBody UpdateUserRequest request) {
        UserVO userVO = userService.update(me, request);
        return new UserRecord(userVO);
    }
}
