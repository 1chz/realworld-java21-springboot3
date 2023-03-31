package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ModelAndView signUp(@Valid @RequestBody UserVO requests, HttpServletRequest httpServletRequest) {
        User created = userService.signUp(requests);
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", created);
    }

    @GetMapping("/api/user")
    public UserVO getUser(User user) {
        return user.toImmutable();
    }

    @PutMapping("/api/user")
    public UserVO updateUser(@RequestBody UserVO updateRequests, User user) {
        return userService.updateUser(user, updateRequests).toImmutable();
    }
}
