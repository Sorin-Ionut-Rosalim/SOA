package service;

import domain.entity.Post;
import domain.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import security.Principal;
import security.SecurityUtils;

import java.util.List;

@Service
public class PostService {

  private final PostRepository postRepository;

  @Autowired
  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }


  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  public List<Post> getAllByUsername(String username) {
    return  postRepository.findAllByUsername(username);
  }

  public Post savePost(Post post) {
    post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
    post.setLikesCount(0);
    return postRepository.save(post);
  }


}
