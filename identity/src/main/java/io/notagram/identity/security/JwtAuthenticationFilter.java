package io.notagram.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final Key key;


  private final String internalServiceToken;

  public JwtAuthenticationFilter(String secretKey, String internalServiceToken) {
    // Decode the secret key (assumed to be Base64 encoded)
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    this.internalServiceToken = internalServiceToken;
    log.debug("Initialized JwtAuthenticationFilter with provided secret key");
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    log.debug("JwtAuthenticationFilter checking URI: {}", path);
    return path.startsWith("/public")
            || path.startsWith("/api/auth")
            || path.startsWith("/error")
            || path.startsWith("/api/session");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    log.debug("Entering JwtAuthenticationFilter for URI: {}", request.getRequestURI());

    // Check for internal service token via Authorization header
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      log.debug("Found Authorization header with token: {}", token);
      if (token.equals(internalServiceToken)) {
        log.info("Internal service request for URI: {}", request.getRequestURI());
        Principal principal = new Principal("internal");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
        return;
      }
    }

    // Check if cookies are present
    if (Objects.isNull(request.getCookies())) {
      log.warn("No cookies found in request to {}", request.getRequestURI());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("couldn't find \"notagram-auth-token\" cookie");
      return;
    }

    // Look for the specific auth cookie
    List<Cookie> cookies = List.of(request.getCookies());
    Cookie authCookie = cookies.stream()
            .filter(cookie -> "notagram-auth-token".equals(cookie.getName()))
            .findAny()
            .orElse(null);

    if (Objects.isNull(authCookie)) {
      log.warn("Cookie 'notagram-auth-token' not found in request to {}", request.getRequestURI());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("couldn't find \"notagram-auth-token\" cookie");
      return;
    }

    log.debug("Found auth cookie with value: {}", authCookie.getValue());

    // Parse and validate the JWT token from the cookie
    String token = authCookie.getValue();
    Claims claims;
    try {
      claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
      log.debug("Token successfully parsed, claims: {}", claims);
    } catch (Exception e) {
      log.error("Failed to parse token: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(e.getMessage());
      return;
    }

    String username = claims.getSubject();
    Principal principal = new Principal(username);
    log.info("User '{}' authenticated for request {}", username, request.getRequestURI());

    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(principal, null, null);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    log.debug("Exiting JwtAuthenticationFilter for URI: {}", request.getRequestURI());
    filterChain.doFilter(request, response);
  }
}
