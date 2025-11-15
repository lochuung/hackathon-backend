package vn.hackathon.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import vn.hackathon.backend.entity.User;

@Service
@RequiredArgsConstructor
public class JwtService {
  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  @Value("${application.jwt.accessToken.expiration:86400}")
  private int accessTokenExpirationTime;

  @Value("${application.url.backend:http://localhost:8080}")
  private String issuer;

  /**
   * Generates a signed JWT access token for the given user containing standard and
   * application-specific claims.
   *
   * <p>The token is built from the user's details (subject, userId, email, roles, avatarUrl,
   * fullName, etc.) and is issued with the service's configured access-token expiration.
   *
   * @param userDetails the authenticated user's details; must be a non-null instance of the
   *     application's User type
   * @return the encoded JWT access token string
   * @throws IllegalArgumentException if {@code userDetails} is null, not an instance of the
   *     expected User class, or if the configured access token expiration is not positive
   */
  public String generateAccessToken(UserDetails userDetails) {
    return jwtEncoder
        .encode(JwtEncoderParameters.from(createClaims(userDetails, accessTokenExpirationTime)))
        .getTokenValue();
  }

  /**
   * Extracts the username from the given JWT by returning its `sub` claim.
   *
   * @param token the JWT string to decode
   * @return the `sub` claim value (username), or {@code null} if the claim is not present
   */
  public String extractUsername(String token) {
    return (String) extractClaims(token).get("sub");
  }

  /**
   * Extracts the `userId` claim from the given JWT.
   *
   * @param token the JWT string to decode
   * @return the `userId` claim value, or {@code null} if the claim is not present
   */
  public String extractUserId(String token) {
    return (String) extractClaims(token).get("userId");
  }

  /**
   * Extracts the user's email address from the JWT's "email" claim.
   *
   * @param token the JWT string to decode
   * @return the email value from the token's "email" claim, or {@code null} if the claim is absent
   */
  public String extractEmail(String token) {
    return (String) extractClaims(token).get("email");
  }

  /**
   * Extracts the roles claim from a JWT and returns them as a single comma-separated string.
   *
   * <p>Decodes the token's claims, converts the "roles" claim to a List<String>, and joins the
   * entries with commas (e.g. "ROLE_USER,ROLE_ADMIN"). The input should be a valid JWT string
   * containing a "roles" claim that is convertible to a list of strings.
   *
   * @param token the JWT string to decode
   * @return a comma-separated list of role names; empty string if the roles claim is present but
   *     empty
   */
  public String extractRoles(String token) {
    Object rolesObj = extractClaims(token).get("roles");
    List<String> rolesList = new ObjectMapper().convertValue(rolesObj, new TypeReference<>() {});
    return String.join(",", rolesList);
  }

  /**
   * Decode a JWT and return its claims as a map.
   *
   * @param token the compact JWT string to decode
   * @return a map of claim names to claim values extracted from the token
   */
  private Map<String, Object> extractClaims(String token) {
    return jwtDecoder.decode(token).getClaims();
  }

  /**
   * Builds a JwtClaimsSet for the given user with standard and custom claims used by access/refresh
   * tokens.
   *
   * <p>The returned claims include issuer, subject, issuedAt, expiresAt, and custom claims: -
   * "sub": user's email (also used as subject) - "userId": user's id as a string - "email": user's
   * email - "avatarUrl": empty string if user's avatarUrl is null - "fullName": empty string if
   * user's fullName is null - "roles" and "scope": user's authorities as a string array
   *
   * @param userDetails the authenticated user; must be an instance of the project's User class
   * @param expiredTime token lifetime in seconds; must be > 0
   * @return a JwtClaimsSet populated with the user's claims and expiration information
   * @throws IllegalArgumentException if userDetails is null, not an instance of User, or
   *     expiredTime <= 0
   */
  private JwtClaimsSet createClaims(UserDetails userDetails, int expiredTime) {
    if (userDetails == null) {
      throw new IllegalArgumentException("UserDetails cannot be null");
    }
    if (expiredTime <= 0) {
      throw new IllegalArgumentException("Expiration time must be greater than zero");
    }

    if (!(userDetails instanceof User user)) {
      throw new IllegalArgumentException("UserDetails must be an instance of User");
    }

    // Build authorities from user roles
    String[] authorities =
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

    JwtClaimsSet.Builder claimsSet =
        JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(user.getEmail())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(expiredTime))
            .claim("userId", String.valueOf(user.getId()))
            .claim("email", user.getEmail())
            .claim("fullName", user.getFullName() == null ? "" : user.getFullName())
            .claim("roles", authorities)
            .claim("scope", authorities);

    return claimsSet.build();
  }

  public UUID getUserId(JwtAuthenticationToken authentication) {
    String userIdStr = (String) authentication.getTokenAttributes().get("userId");
    return UUID.fromString(userIdStr);
  }

  public UUID getUserId() {
    JwtAuthenticationToken authentication =
        (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    String userIdStr = (String) authentication.getTokenAttributes().get("userId");
    return UUID.fromString(userIdStr);
  }

  /**
   * Checks whether a JWT string is decodable by the configured JwtDecoder.
   *
   * <p>Returns true when the provided token is non-blank and can be successfully decoded; returns
   * false for blank tokens or when decoding fails for any reason.
   *
   * @param token the JWT string to validate
   * @return true if the token can be decoded, false otherwise
   */
  public boolean isValidToken(String token) {
    if (StringUtils.isBlank(token)) {
      return false;
    }

    try {
      jwtDecoder.decode(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the avatar URL stored in the token's "avatarUrl" claim.
   *
   * @param token the JWT token string
   * @return the avatar URL from the token, or {@code null} if the claim is not present
   */
  public String extractAvatarUrl(String token) {
    return (String) extractClaims(token).get("avatarUrl");
  }

  /**
   * Extracts the user's full name from the provided JWT.
   *
   * @param token the serialized JWT from which to read the `fullName` claim
   * @return the `fullName` claim value, or {@code null} if the claim is not present
   */
  public String extractFullName(String token) {
    return (String) extractClaims(token).get("fullName");
  }
}
