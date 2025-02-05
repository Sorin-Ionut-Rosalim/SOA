package controller;


import domain.entity.Post;
import domain.entity.PostDTO;
import domain.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import security.Principal;
import security.SecurityUtils;
import service.PostService;
import service.UserService;

import java.util.List;
import java.util.function.Predicate;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/post")
public class PostController {

  private final PostService postService;
  private final UserService userService;

  @Autowired
  public PostController(PostService postService, UserService userService) {
    this.postService = postService;
    this.userService = userService;
  }

  @RequestMapping("/")
  public List<PostDTO> getAllPosts() {
    Principal principal = SecurityUtils.getAuthenticatedUser();

    return postService.getAllPosts().stream().map(post -> postToDto(post, principal)).toList();
  }

  @PostMapping("/")
  public PostDTO createPost(@RequestBody Post post) {
    Principal principal = SecurityUtils.getAuthenticatedUser();
    post.setUsername(principal.getUsername());
    Post savedPost = postService.savePost(post);
    PostDTO postDTO = new PostDTO(savedPost);
    postDTO.setUserAvatar(principal.getProfilePic());
    return postDTO;
  }

  private PostDTO postToDto(Post post, Principal principal) {
    PostDTO postDTO = new PostDTO(post);
    postDTO.setLiked(post.getLikedUserIds().stream().anyMatch(Predicate.isEqual(principal.getUsername())));

    UserDTO userDTO = userService.findUserByUsername(postDTO.getUsername());
    postDTO.setUserAvatar(userDTO.getProfilePic());

    return postDTO;
  }
}
