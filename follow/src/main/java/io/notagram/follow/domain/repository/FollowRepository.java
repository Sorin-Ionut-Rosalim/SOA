package io.notagram.follow.domain.repository;


import io.notagram.follow.domain.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FollowRepository extends JpaRepository<Follow, Long> {


  void deleteFollowByFolloweeAndFollower(String followee, String follower);

  boolean existsByFollowerAndFollowee(String followerId, String followeeId);

  List<Follow> findFollowersByFollowee(String followeeId);

  boolean existsFollowsByFollowee(String followee);
}