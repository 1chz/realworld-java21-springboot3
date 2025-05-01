package io.zhc1.realworld.mixin;

import io.zhc1.realworld.config.AuthToken;

/**
 * A mixin interface that provides authentication-related capabilities. It can be implemented by classes requiring the
 * ability to determine the authentication status of a user.
 *
 * <p>This interface contains a utility method to check whether a given authentication token corresponds to an anonymous
 * (unauthenticated) user.
 */
public interface AuthenticationAwareMixin {
    /**
     * Checks if the provided authentication token represents an anonymous user. An anonymous user is determined if the
     * token is either null or not authenticated.
     *
     * @param token the authentication token to evaluate, can be null
     * @return true if the token is null or not authenticated, false otherwise
     */
    default boolean isAnonymousUser(AuthToken token) {
        return token == null || !token.isAuthenticated();
    }
}
