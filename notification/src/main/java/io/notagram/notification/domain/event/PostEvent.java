package io.notagram.notification.domain.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.notagram.notification.domain.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent extends EventBase {
  private Long postId;
  private String username;
  private Long timestamp;


}
