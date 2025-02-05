package io.notagram.follow.service;

import io.notagram.follow.domain.entity.Follow;
import io.notagram.follow.domain.event.FollowEvent;
import io.notagram.follow.domain.event.FollowEventAction;
import io.notagram.follow.domain.repository.FollowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class FollowService {
  private final FollowRepository followRepository;
  private final RabbitTemplate rabbitTemplate;


  public FollowService(FollowRepository followRepository, RabbitTemplate rabbitTemplate) {
    this.followRepository = followRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  public void followUser(String followerId, String followeeId) {

    if (followerId.equals(followeeId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot follow yourself");
    }

    followRepository.save(new Follow(followerId, followeeId));
    FollowEvent event = new FollowEvent(followerId, followeeId, FollowEventAction.FOLLOW);
    rabbitTemplate.convertAndSend("follow-exchange", "follow.notification", event);
  }

  @Transactional
  public void unfollowUser(String followerId, String followeeId) {
    followRepository.deleteFollowByFolloweeAndFollower(followeeId, followerId);
    FollowEvent event = new FollowEvent(followerId, followeeId, FollowEventAction.UNFOLLOW);
    rabbitTemplate.convertAndSend("follow-exchange", "follow.notification", event);
  }

  public boolean isFollowing(String followerId, String followeeId) {
    return followRepository.existsByFollowerAndFollowee(followerId, followeeId);
  }

  public List<String> getFollowers(String followeeId) {
    return followRepository.findFollowersByFollowee(followeeId).stream().map(Follow::getFollower).toList();
  }
}
