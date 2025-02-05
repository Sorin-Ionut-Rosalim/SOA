package io.notagram.notification.domain.event;

import io.notagram.notification.domain.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventBase {
  EventType type;
}
