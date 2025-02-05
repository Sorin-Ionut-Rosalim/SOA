package io.notagram.identity.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class UserDTO {
  private String username;
  private String profilePic;


  public UserDTO(User user) {
    this.username = user.getUsername();

    if (Objects.isNull(user.getProfilePic())) {
      this.profilePic = String.format("https://picsum.photos/seed/%s/256/256", this.username);
    } else {
      this.profilePic = user.getProfilePic();
    }
  }
}
