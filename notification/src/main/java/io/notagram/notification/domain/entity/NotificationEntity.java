package io.notagram.notification.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "notifications")
public class NotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;      // The user who should receive this notification
  private String content;     // Notification text or payload

  private boolean delivered;  // Flag to indicate if the notification was delivered

  // Optional: Store a creation timestamp
  private Instant createdAt;

  public NotificationEntity() {
    this.createdAt = Instant.now();
  }

}
