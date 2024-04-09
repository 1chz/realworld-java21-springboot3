package sample.shirohoo.realworld.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sample.shirohoo.realworld.core.model.PasswordEncoder;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserRegistry;
import sample.shirohoo.realworld.core.model.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService sut;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void getUserByIdValidUser() {
        // given
        UUID testUuid = UUID.randomUUID();
        User testUser = new User("email", "username", "password");
        when(userRepository.findById(testUuid)).thenReturn(Optional.of(testUser));

        // when
        User result = sut.getUser(testUuid);

        // then
        assertEquals(testUser, result);
    }

    @Test
    void getUserByIdInValidUser() {
        // given
        UUID testUuid = UUID.randomUUID();
        when(userRepository.findById(testUuid)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> sut.getUser(testUuid));
    }

    @Test
    void getUserByUsernameValidUser() {
        // given
        String testUsername = "sampleUsername";
        User testUser = new User("email", "username", "password");
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // when
        User result = sut.getUser(testUsername);

        // then
        assertEquals(testUser, result);
    }

    @Test
    void getUserByUsernameInvalidUser() {
        // given
        String testUsername = "sampleUsername";
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> sut.getUser(testUsername));
    }

    @Test
    void signupUserAlreadyExists() {
        // given
        UserRegistry testRegistry = new UserRegistry("email", "username", "password");
        when(userRepository.existsBy(testRegistry.email(), testRegistry.username()))
                .thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.signup(testRegistry));
    }

    @Test
    void signupValidUser() {
        // given
        UserRegistry testRegistry = new UserRegistry("email", "username", "password");
        when(userRepository.existsBy(testRegistry.email(), testRegistry.username()))
                .thenReturn(false);

        User testUser = new User(testRegistry);
        testUser.setPassword(passwordEncoder, testRegistry.password());
        when(userRepository.save(testUser)).thenReturn(testUser);

        // when
        User result = sut.signup(testRegistry);

        // then
        assertEquals(testUser, result);
    }

    @Test
    void loginUserSuccess() {
        // given
        String testEmail = "testEmail";
        String testPassword = "testPassword";
        User testUser = new User(testEmail, "username", testPassword);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, testUser.getPassword())).thenReturn(true);

        // when
        User result = sut.login(testEmail, testPassword);

        // then
        assertEquals(testUser, result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void loginUserNullOrEmptyEmail(String email) {
        assertThrows(IllegalArgumentException.class, () -> sut.login(email, "testPassword"));
    }

    @Test
    void loginUserInvalidEmail() {
        // given
        String testEmail = "invalidEmail";
        String testPassword = "testPassword";
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.login(testEmail, testPassword));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void loginUserNullOrEmptyPassword(String password) {
        assertThrows(IllegalArgumentException.class, () -> sut.login("testEmail", password));
    }

    @Test
    void loginUserInvalidPassword() {
        // given
        String testEmail = "testEmail";
        String invalidPassword = "invalidPassword";
        User testUser = new User(testEmail, "username", "testPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(invalidPassword, testUser.getPassword())).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.login(testEmail, invalidPassword));
    }

    @Test
    void updateUserDetailsSuccessScenario() {
        // given
        UUID testUuid = UUID.randomUUID();
        String testEmail = "testEmail";
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        String testBio = "testBio";
        String testImageUrl = "testImageUrl";

        User initialUser = new User(testEmail, testUsername, testPassword);
        initialUser.setBio(testBio);
        initialUser.setImageUrl(testImageUrl);

        when(userRepository.updateUserDetails(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(initialUser);

        // when
        User result = sut.updateUserDetails(testUuid, testEmail, testUsername, testPassword, testBio, testImageUrl);

        // then
        assertEquals(initialUser, result);
    }

    @Test
    void updateUserDetailsWithNullID() {
        // given & when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> sut.updateUserDetails(
                        null, "testEmail", "testUsername", "testPassword", "testBio", "testImageUrl"));
    }
}
