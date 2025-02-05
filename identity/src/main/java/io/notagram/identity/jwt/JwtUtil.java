package io.notagram.identity.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.notagram.identity.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

  private final SecretKey secretKey;

  public JwtUtil(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(User user) {
    return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
  }

  public String extractUsername(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    return claims.getSubject();
  }

  public boolean isTokenExpired(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(secretKey)
              .build()
              .parseClaimsJws(token)
              .getBody();
      Date expiration = claims.getExpiration();
      return expiration != null && expiration.before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      log.error("Unexpected parse token error {}", e.getMessage());
      return true;
    }
  }
}
