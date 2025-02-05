package io.notagram.identity.controller;

import io.notagram.identity.entity.User;
import io.notagram.identity.entity.UserDTO;
import io.notagram.identity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{username}")
  public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
    log.info("Received request to get user info for username: {}", username);
    User user = userService.getUserByUsername(username)
            .orElseThrow(() -> {
              log.warn("User '{}' not found", username);
              return new RuntimeException("User not found");
            });
    log.info("Successfully retrieved info for username: {}", username);
    return ResponseEntity.ok(new UserDTO(user));
  }
}
