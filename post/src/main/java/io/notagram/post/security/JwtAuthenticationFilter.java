package io.notagram.post.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.notagram.post.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


  private final String internalServiceToken;

  @Value("${jwt.secret}")
  private String jwtSecret;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(String secretKey, String internalServiceToken) {
    this.internalServiceToken = internalServiceToken;
    // Decode the Base64 encoded secret key
    this.jwtUtil = new JwtUtil(secretKey);
  }


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/public")
            || request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    log.info("Filtering {}", request.getRequestURI());
    if (!Objects.isNull(request.getHeader(HttpHeaders.AUTHORIZATION))) {
      String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

      if (token.equals(internalServiceToken)) {
        log.info("Internal service request {}", request.getRequestURI());
        Principal principal = new Principal("internal");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
        return;
      }
    }

    if (Objects.isNull(request.getCookies())) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("couldn't find \"notagram-auth-token\" cookie");
      return;
    }

    List<Cookie> cookies = List.of(request.getCookies());
    Cookie authCookie =
            cookies.stream().filter(cookie -> cookie.getName().equals("notagram-auth-token")).findAny().orElse(null);

    if (Objects.isNull(authCookie)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("couldn't find \"notagram-auth-token\" cookie");
      return;
    }


    log.debug("Filtering request {}", request);
    String token = authCookie.getValue();

    if (jwtUtil.isTokenExpired(token)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("auth token expired");
      return;
    }

    Principal principal = jwtUtil.extractPrincipal(token);

    log.info("{} authenticated for request {}", principal.getUsername(), request.getRequestURI());

    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(principal, null, null);

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(request, response);

  }
}
