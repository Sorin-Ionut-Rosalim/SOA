package io.notagram.follow.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.notagram.follow.security.Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {


  private final SecretKey secretKey;

  public JwtUtil(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public Principal extractPrincipal(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    return new Principal(
            claims.getSubject()
    );
  }

  public boolean isTokenExpired(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    Date expiration = claims.getExpiration();
    return expiration != null && expiration.before(new Date());
  }
}
