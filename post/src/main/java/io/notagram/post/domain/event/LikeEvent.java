package io.notagram.post.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeEvent {
  private String username;
  private String postAuthor;
  private Long postId;
  private LikeEventAction action;
}
