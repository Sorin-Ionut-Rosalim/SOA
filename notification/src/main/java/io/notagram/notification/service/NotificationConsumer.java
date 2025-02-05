package io.notagram.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.notagram.notification.domain.entity.NotificationEntity;
import io.notagram.notification.domain.entity.EventType;
import io.notagram.notification.domain.event.FollowEvent;
import io.notagram.notification.domain.event.LikeEvent;
import io.notagram.notification.domain.event.PostEvent;
import io.notagram.notification.domain.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationConsumer {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final FollowService followService;
  private final NotificationService notificationService;

  @Autowired
  public NotificationConsumer(UserSessionService userSessionService, SimpMessagingTemplate messagingTemplate,
                              NotificationRepository notificationRepository, FollowService followService,
                              NotificationService notificationService) {
    this.followService = followService;
    this.notificationService = notificationService;
  }

  @KafkaListener(topics = "post-events", groupId = "notification-service")
  public void handlePostEventFromKafka(String messagePayload) throws JsonProcessingException {
    log.info("consumed message {}", messagePayload);

    try {
      String notificationPayload;
      PostEvent postEvent = objectMapper.readValue(messagePayload, PostEvent.class);
      postEvent.setType(EventType.NEW_POST);
      notificationPayload = objectMapper.writeValueAsString(postEvent);
      followService.getFollowers(postEvent.getUsername()).forEach((follower -> {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setDelivered(false);
        notificationEntity.setUsername(follower);
        notificationEntity.setContent(notificationPayload);
        notificationService.tryToSendNotification(notificationEntity);
      }));
    } catch (Exception e) {
      log.error(e.getMessage());
      return;
    }


  }

  @RabbitListener(queues = "follow-notifications-queue")
  public void handleFollowEventFromRabbit(String messagePayload) {
    log.info("Received follow message from rabbitmq queue: {}", messagePayload);
    try {
      FollowEvent followEvent = objectMapper.readValue(messagePayload, FollowEvent.class);
      followEvent.setType(EventType.FOLLOW);

      String notificationPayload = objectMapper.writeValueAsString(followEvent);

      NotificationEntity notification = new NotificationEntity();
      notification.setDelivered(false);
      notification.setUsername(followEvent.getFollowee());
      notification.setContent(notificationPayload);
      notificationService.tryToSendNotification(notification);

    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }
  @RabbitListener(queues = "like-notifications-queue")
  public void handleLikeEventFromRabbit(String messagePayload) {
    log.info("Received like message from rabbitmq queue: {}", messagePayload);
    try {
      LikeEvent likeEvent = objectMapper.readValue(messagePayload, LikeEvent.class);
      likeEvent.setType(EventType.LIKE);

      if (likeEvent.getPostAuthor().equals(likeEvent.getUsername())) {
        log.info("{} {}d his own post...", likeEvent.getPostAuthor(), likeEvent.getAction().toString().toLowerCase());
        return;
      }

      Boolean following = followService.isFollowing(likeEvent.getPostAuthor(), likeEvent.getUsername());
      if (!following) {
        log.info("{} not following {}, don't send like notification", likeEvent.getPostAuthor(),
                likeEvent.getUsername());
        return;
      }

      String notificationPayload = objectMapper.writeValueAsString(likeEvent);

      NotificationEntity notification = new NotificationEntity();
      notification.setDelivered(false);
      notification.setUsername(likeEvent.getPostAuthor());
      notification.setContent(notificationPayload);
      notificationService.tryToSendNotification(notification);

    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }


}
