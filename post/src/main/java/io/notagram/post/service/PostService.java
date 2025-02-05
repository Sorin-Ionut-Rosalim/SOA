package io.notagram.post.service;


import io.notagram.post.domain.entity.Post;
import io.notagram.post.domain.event.LikeEvent;
import io.notagram.post.domain.event.LikeEventAction;
import io.notagram.post.domain.event.PostEvent;
import io.notagram.post.domain.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Slf4j
public class PostService {

  private final PostRepository postRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private static final String POST_TOPIC = "post-events";
  private final RabbitTemplate rabbitTemplate;

  @Autowired
  public PostService(PostRepository postRepository, KafkaTemplate<String, Object> kafkaTemplate,
                     RabbitTemplate rabbitTemplate) {
    this.postRepository = postRepository;
    this.kafkaTemplate = kafkaTemplate;
    this.rabbitTemplate = rabbitTemplate;
  }


  public List<Post> getAllPosts() {
    return postRepository.findAll().stream().sorted(
            Comparator.comparing(Post::getCreatedAt)
    ).toList().reversed();
  }

  public Optional<Post> getPostById(Long postId) {
    return postRepository.findById(postId);
  }

  public List<Post> getAllByUsername(String username) {
    return postRepository.findAllByUsername(username).stream().sorted(
            Comparator.comparing(Post::getCreatedAt)
    ).toList().reversed();
  }


  public Post savePost(Post post) {
    Long now = System.currentTimeMillis();
    post.setCreatedAt(String.valueOf(now));
    post.setLikesCount(0);
    Post saved = postRepository.save(post);

    PostEvent postEvent = new PostEvent(saved.getId(), saved.getUsername(), now);

    log.info("Sending event {} to {}", postEvent, POST_TOPIC);
    kafkaTemplate.send(POST_TOPIC, postEvent).whenComplete((result, e) -> {
      if (e != null) {
        log.error("Exception when sending post kafka event: {}", e.getMessage());
        return;
      }

      log.info("Kafka send post event result producer record: {}, record metadata: {}", result.getProducerRecord(),
              result.getRecordMetadata());
    });

    return saved;
  }

  public Post likePost(Long postId, String username) {

    Post post = getPostById(postId).orElseThrow(() -> new RuntimeException("post not found"));

    Set<String> likedUserIds = post.getLikedUserIds();

    if (likedUserIds.contains(username)) {
      return post;
    }

    likedUserIds.add(username);
    post.setLikedUserIds(likedUserIds);
    post.setLikesCount(likedUserIds.size());

    Post saved = postRepository.save(post);

    rabbitTemplate.convertAndSend("like-exchange", "like.notification", new LikeEvent(
            username,
            saved.getUsername(),
            postId,
            LikeEventAction.LIKE
    ));

    return saved;

  }

  public Post dislikePost(Long postId, String username) {

    Post post = getPostById(postId).orElseThrow(() -> new RuntimeException("post not found"));

    Set<String> likedUserIds = post.getLikedUserIds();

    if (!likedUserIds.contains(username)) {
      return post;
    }

    likedUserIds.remove(username);
    post.setLikedUserIds(likedUserIds);
    post.setLikesCount(likedUserIds.size());


    Post saved = postRepository.save(post);


    rabbitTemplate.convertAndSend("like-exchange", "like.notification", new LikeEvent(
            username,
            saved.getUsername(),
            postId,
            LikeEventAction.DISLIKE
    ));


    return saved;
  }


}
