package io.notagram.identity.controller;

import io.notagram.identity.entity.User;
import io.notagram.identity.entity.UserDTO;
import io.notagram.identity.jwt.JwtUtil;
import io.notagram.identity.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;
  private final JwtUtil jwtUtil;

  public AuthController(UserService userService, JwtUtil jwtUtil) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }


  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody User registerUser) {
    try {
      User user = userService.register(registerUser);
      return ResponseEntity.ok(new UserDTO(user));
    } catch (Exception e) {
      log.error("Error {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<UserDTO> login(@RequestBody Map<String, String> request) {
    String username = request.get("username");
    String password = request.get("password");
    User user = userService.authenticate(username, password);
    String token = jwtUtil.generateToken(user);

// Create a ResponseCookie
    ResponseCookie cookie = ResponseCookie.from("notagram-auth-token", token).httpOnly(true).secure(true).path("/")
            // .domain("localhost") // optionally
            // "None" if you want to allow cross-site usage
            .sameSite("None").build();

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new UserDTO(user));
  }


  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  public void logout(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie
            .from("notagram-auth-token", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(0) // maxAge=0 instructs the browser to delete it
            .build();

    // Add the Set-Cookie header to the response
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    // No return value needed; Spring returns 200 OK with no body.
  }

}
