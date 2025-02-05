package io.notagram.notification.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Principal {
  private String username;

  @Override
  public String toString() {
    return this.username;
  }
}
