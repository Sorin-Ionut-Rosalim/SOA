package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import security.SecurityUtils;

import java.util.List;

@Configuration
public class RestTemplateConfig {

  @Value("${internal.service.token}")
  private String internalServiceToken;

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add((httpRequest, bytes, clientHttpRequestExecution) -> {
      // Set your internal token or custom header
      httpRequest.getHeaders().add("Authorization", internalServiceToken);
      // Optionally, add user info if needed:
       httpRequest.getHeaders().add("X-User-ID", SecurityUtils.getAuthenticatedUser().getUsername());
      return clientHttpRequestExecution.execute(httpRequest, bytes);
    });
    return restTemplate;
  }

}
