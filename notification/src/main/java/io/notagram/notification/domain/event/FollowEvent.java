package io.notagram.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowEvent extends EventBase {
  private String follower;
  private String followee;
  private FollowEventAction action;


}
