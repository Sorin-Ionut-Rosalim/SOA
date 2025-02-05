package io.notagram.post.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent {
  private Long postId;
  private String username;
  private Long timestamp;
}
