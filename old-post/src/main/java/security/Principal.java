package security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Principal {
  private String username;
  private String profilePic;
}
