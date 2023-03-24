package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    @PostMapping("/api/users")
    public ModelAndView register(@RequestBody @Valid UserRequest requests, HttpServletRequest httpServletRequest) {
        registerService.register(requests.toUser());

        UserResponse loginRequest = new UserResponse(requests);
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", loginRequest);
    }

    private record UserRequest(@Valid UserRequestDetails user) {
        public User toUser() {
            return User.builder()
                    .email(user.email)
                    .username(user.username)
                    .password(user.password)
                    .build();
        }
    }

    private record UserRequestDetails(
            @Email @NotBlank String email, @NotBlank String password, @NotBlank String username) {}

    private record UserResponse(UserResponseDetails user) {
        public UserResponse(UserRequest userRequest) {
            this(new UserResponseDetails(userRequest.user.email, userRequest.user.password));
        }
    }

    private record UserResponseDetails(String email, String password) {}
}
