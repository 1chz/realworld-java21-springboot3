package sample.shirohoo.realworld.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

class InterceptorsExceptionHandlerTest {
    InterceptorsExceptionHandler sut;

    @BeforeEach
    void setUp() {
        sut = new InterceptorsExceptionHandler();
    }

    @Test
    void handle_IllegalArgumentException() {
        ProblemDetail problem = sut.handle(new IllegalArgumentException("bad request"));
        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getDetail()).isEqualTo("bad request");
    }

    @Test
    void handle_NoSuchElementException() {
        ProblemDetail problem = sut.handle(new NoSuchElementException("not found"));
        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getDetail()).isEqualTo("not found");
    }

    @Test
    void handle_AccessDeniedException() {
        ProblemDetail problem = sut.handle(new AccessDeniedException("forbidden"));
        assertThat(problem.getStatus()).isEqualTo(403);
        assertThat(problem.getDetail()).isEqualTo("forbidden");
    }

    @Test
    void handle_AuthenticationException() {
        ProblemDetail problem = sut.handle(new AuthenticationException("unauthorized") {});
        assertThat(problem.getStatus()).isEqualTo(401);
        assertThat(problem.getDetail()).isEqualTo("unauthorized");
    }

    @Test
    void handle_Exception() {
        ProblemDetail problem = sut.handle(new Exception("unexpected"));
        assertThat(problem.getStatus()).isEqualTo(500);
        assertThat(problem.getDetail()).isEqualTo("Please contact the administrator.");
    }
}
