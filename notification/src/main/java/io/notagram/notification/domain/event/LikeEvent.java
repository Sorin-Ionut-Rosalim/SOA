package io.notagram.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent extends EventBase {
  private String username;
  private String postAuthor;
  private Long postId;
  private LikeEventAction action;


}
