package io.zhc1.realworld.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@DisplayName("Application Exception Handler - HTTP Error Response Mapping")
class ApplicationExceptionHandlerTest {
    ApplicationExceptionHandler sut;

    @BeforeEach
    void setUp() {
        sut = new ApplicationExceptionHandler();
    }

    @Test
    @DisplayName("When handling IllegalArgumentException, then should return 400 Bad Request")
    void whenHandlingIllegalArgumentException_thenShouldReturn400BadRequest() {
        ProblemDetail problem = sut.handle(new IllegalArgumentException("bad request"));
        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getDetail()).isEqualTo("bad request");
    }

    @Test
    @DisplayName("When handling NoSuchElementException, then should return 404 Not Found")
    void whenHandlingNoSuchElementException_thenShouldReturn404NotFound() {
        ProblemDetail problem = sut.handle(new NoSuchElementException("not found"));
        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getDetail()).isEqualTo("not found");
    }

    @Test
    @DisplayName("When handling AccessDeniedException, then should return 403 Forbidden")
    void whenHandlingAccessDeniedException_thenShouldReturn403Forbidden() {
        ProblemDetail problem = sut.handle(new AccessDeniedException("forbidden"));
        assertThat(problem.getStatus()).isEqualTo(403);
        assertThat(problem.getDetail()).isEqualTo("forbidden");
    }

    @Test
    @DisplayName("When handling AuthenticationException, then should return 401 Unauthorized")
    void whenHandlingAuthenticationException_thenShouldReturn401Unauthorized() {
        ProblemDetail problem = sut.handle(new AuthenticationException("unauthorized") {});
        assertThat(problem.getStatus()).isEqualTo(401);
        assertThat(problem.getDetail()).isEqualTo("unauthorized");
    }

    @Test
    @DisplayName("When handling unexpected exceptions, then should return 500 Internal Server Error")
    void whenHandlingUnexpectedExceptions_thenShouldReturn500InternalServerError() {
        ProblemDetail problem = sut.handle(new Exception("unexpected"));
        assertThat(problem.getStatus()).isEqualTo(500);
        assertThat(problem.getDetail()).isEqualTo("Please contact the administrator.");
    }
}
