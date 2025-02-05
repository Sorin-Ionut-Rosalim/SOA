package domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;


@Entity
@Data
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 256)
  private String description;

  @Column(name = "username")
  private String username;

  private String picUrl;
  private String createdAt;

  @Column(columnDefinition = "int default 0")
  private Integer likesCount = 0;

  // Store user IDs of those who liked the post.
  // The join table "post_likes" will have columns "post_id" and "username".
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
  @Column(name = "username")
  private Set<String> likedUserIds = new HashSet<>();
}
