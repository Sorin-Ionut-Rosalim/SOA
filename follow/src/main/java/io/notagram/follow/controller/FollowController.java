package io.notagram.follow.controller;


import io.notagram.follow.security.SecurityUtils;
import io.notagram.follow.service.FollowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/follow")
public class FollowController {

  private final FollowService followService;

  public FollowController(FollowService followService) {
    this.followService = followService;
  }

  @PostMapping("/{followeeId}")
  public void followUser(@PathVariable String followeeId) {
    String followerId = SecurityUtils.getAuthenticatedUser().getUsername();
    followService.followUser(followerId, followeeId);
  }

  @DeleteMapping("/{followeeId}")
  public void unfollowUser(@PathVariable String followeeId) {
    String followerId = SecurityUtils.getAuthenticatedUser().getUsername();
    followService.unfollowUser(followerId, followeeId);
  }

  @GetMapping("/{followeeId}/follows/{followerId}")
  public boolean isFollowing(@PathVariable String followeeId, @PathVariable String followerId) {
    return followService.isFollowing(followerId, followeeId);
  }

  @GetMapping("/followers/{username}")
  public List<String> getFollowerByUsername(@PathVariable String username) {
    return followService.getFollowers(username);
  }


}
