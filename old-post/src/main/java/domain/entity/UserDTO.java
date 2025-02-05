package domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class UserDTO {
  private String username;
  private String profilePic;
}
