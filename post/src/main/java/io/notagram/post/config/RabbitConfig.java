package io.notagram.post.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Bean
  public TopicExchange likeExchange() {
    return new TopicExchange("like-exchange");
  }

  @Bean
  public Queue likeNotificationQueue() {
    return new Queue("like-notifications-queue", true);
  }

  @Bean
  public Binding likeNotificationBinding(
          TopicExchange likeExchange,
          Queue likeNotificationQueue
  ) {
    return BindingBuilder
            .bind(likeNotificationQueue)
            .to(likeExchange)
            .with("like.notification");
  }


  // 1) Provide a Jackson2JsonMessageConverter bean
  @Bean
  public MessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // 2) Let RabbitTemplate use Jackson2Json for message conversion
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                       MessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }
}
