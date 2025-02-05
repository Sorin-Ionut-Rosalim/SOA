package io.notagram.notification.config;


import io.notagram.notification.jwt.JwtUtil;
import io.notagram.notification.security.Principal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  // ...

  JwtUtil jwtUtil;

  public WebSocketConfig(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Where the client subscribes for messages
    registry.enableSimpleBroker("/topic", "/queue");

    // Prefix for messages from client to server
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000") // or wherever
            .withSockJS()
            .setInterceptors(httpSessionHandshakeInterceptor());
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(Objects.requireNonNull(accessor).getCommand())) {
          // Session attributes were set by the HandshakeInterceptor
          Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
          String token = (String) Objects.requireNonNull(sessionAttributes).get("JWT_TOKEN");

          if (token == null) {
            throw new IllegalArgumentException("Missing token cookie in session attributes");
          }

          try {
            // 1) Validate/parse the token
            //    E.g., use your JwtUtil (like your existing code):
            Principal principal = jwtUtil.extractPrincipal(token);

            // 2) Create an Authentication (or you can store the Principal directly)
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(principal, null, null);

            // 3) Attach it to the StompHeaderAccessor user
            accessor.setUser(authenticationToken);

            // Optionally set the SecurityContext for consistency
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

          } catch (Exception e) {
            // If invalid or expired token, reject the connection
            throw new IllegalArgumentException("Invalid or expired token in STOMP CONNECT", e);
          }
        }

        return message;
      }
    });
  }

  @Bean
  public HandshakeInterceptor httpSessionHandshakeInterceptor() {
    return new HandshakeInterceptor() {
      @Override
      public boolean beforeHandshake(
              ServerHttpRequest request,
              ServerHttpResponse response,
              WebSocketHandler wsHandler,
              Map<String, Object> attributes
      ) throws Exception {

        if (request instanceof ServletServerHttpRequest servletServerRequest) {
          HttpServletRequest servletRequest = servletServerRequest.getServletRequest();

          // Look for cookie named "notagram-auth-token" (or "key", etc.)
          Cookie tokenCookie = WebUtils.getCookie(servletRequest, "notagram-auth-token");
          if (tokenCookie != null) {
            // Store token in session attributes
            attributes.put("JWT_TOKEN", tokenCookie.getValue());
          }
        }
        return true; // proceed with handshake
      }

      @Override
      public void afterHandshake(
              ServerHttpRequest request,
              ServerHttpResponse response,
              WebSocketHandler wsHandler,
              Exception ex
      ) {
        // no-op
      }
    };
  }
  // ...
}
