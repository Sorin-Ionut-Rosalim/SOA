package io.notagram.identity.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @Column(unique = true)
  private String username;
  private String profilePic;

  private String password;
  private LocalDateTime createdAt = LocalDateTime.now();
}
