package io.notagram.post.controller;

import io.notagram.post.domain.entity.FeedType;
import io.notagram.post.domain.entity.Post;
import io.notagram.post.domain.entity.PostDTO;
import io.notagram.post.domain.entity.UserDTO;
import io.notagram.post.security.Principal;
import io.notagram.post.security.SecurityUtils;
import io.notagram.post.service.FollowService;
import io.notagram.post.service.PostService;
import io.notagram.post.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import java.util.Objects;
import java.util.function.Predicate;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/post")
public class PostController {

  private static final Logger log = LoggerFactory.getLogger(PostController.class);

  private final PostService postService;
  private final UserService userService;
  private final FollowService followService;

  @Autowired
  public PostController(PostService postService, UserService userService, FollowService followService) {
    this.postService = postService;
    this.userService = userService;
    this.followService = followService;
  }

  @RequestMapping()
  public List<PostDTO> getAllPosts(
          @RequestParam(name = "feedType", required = false, defaultValue = "ALL") FeedType feedType,
          @RequestParam(name = "username", required = false, defaultValue = "") String username
  ) {
    Principal principal = SecurityUtils.getAuthenticatedUser();
    log.info("Fetching all posts for user: {}", principal.getUsername());

    if (!username.isEmpty()) {
      return this.getAllPostsBy(username, principal);
    }

    if (Objects.requireNonNull(feedType) == FeedType.FOLLOWING) {
      return postService.getAllPosts().stream()
              .filter(post -> {
                return followService.isFollowing(principal.getUsername(), post.getUsername());
              })
              .map(post -> postToDto(post, principal))
              .toList();
    }

    return postService.getAllPosts().stream()
            .map(post -> postToDto(post, principal))
            .toList();

  }

  private List<PostDTO> getAllPostsBy(String username, Principal principal) {
    return postService.getAllByUsername(username)
            .stream()
            .map(post -> postToDto(post, principal))
            .toList();
  }


  @PostMapping()
  public PostDTO createPost(@RequestBody Post post) {
    Principal principal = SecurityUtils.getAuthenticatedUser();
    log.info("User {} is creating a new post", principal.getUsername());

    post.setUsername(principal.getUsername());
    Post savedPost = postService.savePost(post);
    log.info("Post saved with id {} by user {}", savedPost.getId(), principal.getUsername());

    PostDTO postDTO = postToDto(savedPost, principal);
    log.info("PostDTO created for post id {}", savedPost.getId());
    return postDTO;
  }

  @PostMapping("/like/{postId}")
  public PostDTO likePost(@PathVariable Long postId) {
    Principal principal = SecurityUtils.getAuthenticatedUser();

    return postToDto(postService.likePost(postId, principal.getUsername()), principal);
  }

  @DeleteMapping("/like/{postId}")
  public PostDTO dislikePost(@PathVariable Long postId) {
    Principal principal = SecurityUtils.getAuthenticatedUser();

    return postToDto(postService.dislikePost(postId, principal.getUsername()), principal);
  }

  private PostDTO postToDto(Post post, Principal principal) {
    log.debug("Converting post with id {} to DTO", post.getId());

    PostDTO postDTO = new PostDTO(post);
    boolean liked = post.getLikedUserIds().stream().anyMatch(Predicate.isEqual(principal.getUsername()));
    postDTO.setLiked(liked);
    log.debug("Set liked flag for post id {}: {}", post.getId(), liked);

    log.debug("Fetching extra metadata for postDTO: {}", postDTO.getId());
    try {
      UserDTO userDTO = userService.findUserByUsername(postDTO.getUsername());
      postDTO.setUserAvatar(userDTO.getProfilePic());
      Boolean following = followService.isFollowing(principal.getUsername(), postDTO.getUsername());
      postDTO.setFollowing(following);

      log.debug("Set user avatar for post id {} using user {}", post.getId(), postDTO.getUsername());
    } catch (Exception e) {
      log.error("Failed to fetch extra metadata for postDTO {}: {}", postDTO.getId(), e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
    return postDTO;
  }
}
