package io.notagram.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class HttpClientConfig {

  @Bean
  public HttpClient httpClient() {
    // Customize your client if needed (timeouts, version, etc.)
    return HttpClient.newBuilder()
            .build();
  }
}
