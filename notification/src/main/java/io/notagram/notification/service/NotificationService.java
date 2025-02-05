package io.notagram.notification.service;


import io.notagram.notification.domain.entity.NotificationEntity;
import io.notagram.notification.domain.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final UserSessionService userSessionService;
  private final SimpMessagingTemplate messagingTemplate;
  private final Logger logger = LoggerFactory.getLogger(NotificationService.class);
  private final NotificationRepository notificationRepository;

  @Autowired
  public NotificationService(UserSessionService userSessionService, SimpMessagingTemplate messagingTemplate,
                             NotificationRepository notificationRepository) {
    this.userSessionService = userSessionService;
    this.messagingTemplate = messagingTemplate;
    this.notificationRepository = notificationRepository;
  }

  public void tryToSendNotification(NotificationEntity notification) {

    logger.info("Received notification for {}", notification.getUsername());

    String username = notification.getUsername();
    if (userSessionService.isUserOnline(username)) {
      logger.info("User {} is online, sending notification", username);
      messagingTemplate.convertAndSendToUser(username, "/queue/notifications",  // or your chosen destination
              notification.getContent());

      return;
    }
    logger.info("User {} is offline, saving notification", username);
    notification.setDelivered(false);
    notificationRepository.save(notification);
  }
}
