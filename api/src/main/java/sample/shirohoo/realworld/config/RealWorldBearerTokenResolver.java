package sample.shirohoo.realworld.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Change the prefix of the Authorization token from 'Bearer' to 'Token'. */
@Component
class RealWorldBearerTokenResolver implements BearerTokenResolver {
    private static final Pattern AUTHORIZATION_PATTERN =
            Pattern.compile("^Token (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    private static final String ACCESS_TOKEN_PARAM = "access_token";

    @Override
    public String resolve(HttpServletRequest request) {
        String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
        String parameterToken =
                isParameterTokenSupportedForRequest(request) ? resolveFromRequestParameters(request) : null;

        if (authorizationHeaderToken != null) {
            if (parameterToken != null) {
                throw new OAuth2AuthenticationException(
                        BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request"));
            }
            return authorizationHeaderToken;
        }

        return parameterToken;
    }

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.startsWithIgnoreCase(authorization, "token")) {
            return null;
        }

        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new OAuth2AuthenticationException(BearerTokenErrors.invalidToken("Bearer token is malformed"));
        }

        return matcher.group("token");
    }

    private boolean isParameterTokenSupportedForRequest(HttpServletRequest request) {
        return (("POST".equals(request.getMethod())
                        && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
                || "GET".equals(request.getMethod()));
    }

    private String resolveFromRequestParameters(HttpServletRequest request) {
        String[] values = request.getParameterValues(ACCESS_TOKEN_PARAM);

        if (values == null || values.length == 0) {
            return null;
        }

        if (values.length == 1) {
            return values[0];
        }

        throw new OAuth2AuthenticationException(
                BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request"));
    }
}
