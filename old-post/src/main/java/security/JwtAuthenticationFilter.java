package security;

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

  private final Key key;
  @Value("${internal.service.token}")
  private String internalServiceToken;

  public JwtAuthenticationFilter(String secretKey) {
    // Decode the Base64 encoded secret key
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
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
        Principal principal = new Principal("internal", "n/a");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
        return;
      }
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
    Claims claims;

    try {
      claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (Exception e) {
      log.error("Failed to parse token {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(e.getMessage());
      return;
    }


    String username = claims.getSubject();
    String profilePic = String.valueOf(claims.get("profile_pic"));
    Principal principal = new Principal(username, profilePic);
    log.info("{} authenticated for request {}", username, request.getRequestURI());

    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(principal, null, null);

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(request, response);

  }
}
