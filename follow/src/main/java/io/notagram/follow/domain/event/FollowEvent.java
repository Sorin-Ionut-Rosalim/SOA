package io.notagram.follow.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowEvent {
  private String follower;
  private String followee;
  private FollowEventAction action;
}
