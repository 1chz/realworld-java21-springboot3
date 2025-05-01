package io.zhc1.realworld.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.zhc1.realworld.model.PasswordEncoder;
import io.zhc1.realworld.model.User;
import io.zhc1.realworld.model.UserRegistry;
import io.zhc1.realworld.model.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("User - User Authentication, Registration, and Profile Management Operations")
class UserServiceTest {
    @InjectMocks
    UserService sut;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Get user by ID should return user when user exists")
    void whenGetUserByIdWithValidId_thenShouldReturnUser() {
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
    @DisplayName("Get user by ID should throw exception when user does not exist")
    void whenGetUserByIdWithInvalidId_thenShouldThrowException() {
        // given
        UUID testUuid = UUID.randomUUID();
        when(userRepository.findById(testUuid)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> sut.getUser(testUuid));
    }

    @Test
    @DisplayName("Get user by username should return user when user exists")
    void whenGetUserByUsernameWithValidUsername_thenShouldReturnUser() {
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
    @DisplayName("Get user by username should throw exception when user does not exist")
    void whenGetUserByUsernameWithInvalidUsername_thenShouldThrowException() {
        // given
        String testUsername = "sampleUsername";
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> sut.getUser(testUsername));
    }

    @Test
    @DisplayName("Signup should throw exception when user already exists")
    void whenSignupWithExistingUser_thenShouldThrowException() {
        // given
        UserRegistry testRegistry = new UserRegistry("email", "username", "password");
        when(userRepository.existsBy(testRegistry.email(), testRegistry.username()))
                .thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.signup(testRegistry));
    }

    @Test
    @DisplayName("Signup should succeed with valid user information")
    void whenSignupWithValidUserInfo_thenShouldSucceed() {
        // given
        UserRegistry testRegistry = new UserRegistry("email", "username", "password");
        when(userRepository.existsBy(testRegistry.email(), testRegistry.username()))
                .thenReturn(false);

        User testUser = new User(testRegistry);
        testUser.encryptPassword(passwordEncoder, testRegistry.password());
        when(userRepository.save(testUser)).thenReturn(testUser);

        // when
        User result = sut.signup(testRegistry);

        // then
        assertEquals(testUser, result);
    }

    @Test
    @DisplayName("Login should succeed with valid credentials")
    void whenLoginWithValidCredentials_thenShouldSucceed() {
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
    @DisplayName("Login should throw exception when email is null or empty")
    void whenLoginWithNullOrEmptyEmail_thenShouldThrowException(String email) {
        assertThrows(IllegalArgumentException.class, () -> sut.login(email, "testPassword"));
    }

    @Test
    @DisplayName("Login should throw exception when email is invalid")
    void whenLoginWithInvalidEmail_thenShouldThrowException() {
        // given
        String testEmail = "invalidEmail";
        String testPassword = "testPassword";
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.login(testEmail, testPassword));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Login should throw exception when password is null or empty")
    void whenLoginWithNullOrEmptyPassword_thenShouldThrowException(String password) {
        assertThrows(IllegalArgumentException.class, () -> sut.login("testEmail", password));
    }

    @Test
    @DisplayName("Login should throw exception when password is invalid")
    void whenLoginWithInvalidPassword_thenShouldThrowException() {
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
    @DisplayName("Update user details should succeed with valid inputs")
    void whenUpdateUserDetailsWithValidInputs_thenShouldSucceed() {
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
    @DisplayName("Update user details should throw exception when ID is null")
    void whenUpdateUserDetailsWithNullId_thenShouldThrowException() {
        // given & when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> sut.updateUserDetails(
                        null, "testEmail", "testUsername", "testPassword", "testBio", "testImageUrl"));
    }
}
