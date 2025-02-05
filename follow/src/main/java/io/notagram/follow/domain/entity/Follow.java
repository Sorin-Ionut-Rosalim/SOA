package io.notagram.follow.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Index this table by follower and followee
@Entity
@Data
@NoArgsConstructor

@Table(name = "follows",
  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower", "followee"})
  },
  indexes = {
    @Index(columnList = "follower"),
    @Index(columnList = "followee")
  })
public class Follow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String follower;
  private String followee;

  public Follow(String follower, String followee) {
    this.follower = follower;
    this.followee = followee;
  }
}
