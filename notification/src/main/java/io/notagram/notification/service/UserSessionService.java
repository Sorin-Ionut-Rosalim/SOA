package io.notagram.notification.service;


import io.notagram.notification.domain.entity.NotificationEntity;
import io.notagram.notification.domain.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.notagram.notification.security.SecurityUtils;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Service
public class UserSessionService implements ApplicationListener<SessionDisconnectEvent> {

  private final ConcurrentMap<String, String> sessionIdToUsername = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, String> usernameToSessionId = new ConcurrentHashMap<>();
  private static final Logger logger =
          LoggerFactory.getLogger(UserSessionService.class);

  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public UserSessionService( NotificationRepository notificationRepository,
                            SimpMessagingTemplate simpMessagingTemplate) {
    this.notificationRepository = notificationRepository;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @EventListener
  public void handleSessionConnectEvent(SessionConnectEvent event) {
    // 1. Extract username from the event or from the StompHeaderAccessor

    StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = sha.getSessionId();

    String username = SecurityUtils.getAuthenticatedUser().getUsername();
    logger.info("New session {} from {}", sessionId, username);
    sessionIdToUsername.put(sessionId, username);
    usernameToSessionId.put(username, sessionId);

  }

  @EventListener
  public void handleSubscribeEvent(SessionSubscribeEvent event) {
    String username = Objects.requireNonNull(event.getUser()).getName();

    ArrayList<NotificationEntity> unreadList = notificationRepository.findAllByUsernameAndDeliveredFalse(username);

    for (NotificationEntity notificationEntity : unreadList) {
      simpMessagingTemplate.convertAndSendToUser(
              username,
              "/queue/notifications",
              notificationEntity.getContent()
      );
      notificationEntity.setDelivered(true);
    }

    notificationRepository.saveAll(unreadList);
  }

  @Override
  public void onApplicationEvent(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    String username = sessionIdToUsername.remove(sessionId);
    logger.info("Removing user {} from session {}", username, sessionId);
    if (username != null) {
      usernameToSessionId.remove(username);
    }
  }

  public boolean isUserOnline(String username) {
    return usernameToSessionId.containsKey(username);
  }

  public String getSessionIdForUser(String username) {
    return usernameToSessionId.get(username);
  }
}
