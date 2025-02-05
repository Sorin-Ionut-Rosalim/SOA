package io.notagram.identity.controller;

import io.notagram.identity.entity.User;
import io.notagram.identity.entity.UserDTO;
import io.notagram.identity.jwt.JwtUtil;
import io.notagram.identity.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/session")
public class SessionController {


  private final JwtUtil jwtUtil;
  private final UserService userService;

  public SessionController(JwtUtil jwtUtil, UserService userService) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @GetMapping("")
  public ResponseEntity<UserDTO> getSession(
          @CookieValue(value = "notagram-auth-token", required = false) String sessionCookie)
          throws ResponseStatusException {
    if (Objects.isNull(sessionCookie)) {
      log.error("auth token cookie not found");
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
    }

    if (jwtUtil.isTokenExpired(sessionCookie)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token expired");
    }

    User user = userService.getUserByUsername(jwtUtil.extractUsername(sessionCookie))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
    String token = jwtUtil.generateToken(user);

// Create a ResponseCookie
    ResponseCookie cookie = ResponseCookie.from("notagram-auth-token", token).httpOnly(true).secure(true).path("/")
            // .domain("localhost") // optionally
            // "None" if you want to allow cross-site usage
            .sameSite("None").build();

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new UserDTO(user));
  }

}
