package domain.entity;


import lombok.Data;

@Data
public class PostDTO {
  private Long id;
  private String description;
  private String username;
  private String picUrl;
  private String createdAt;
  private Integer likesCount;
  private Boolean liked;
  private String userAvatar;
  private Boolean following;

  public PostDTO(Post post) {
    this.id = post.getId();
    this.description = post.getDescription();
    this.username = post.getUsername();
    this.picUrl = post.getPicUrl();
    this.createdAt = post.getCreatedAt();
    this.likesCount = post.getLikesCount();
  }

}
