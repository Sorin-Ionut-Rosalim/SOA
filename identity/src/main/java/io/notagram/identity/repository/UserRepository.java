package io.notagram.identity.repository;

import io.notagram.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  List<User> findByUsernameIn(Collection<String> usernames);

  boolean existsUserByUsername(String username);
}
