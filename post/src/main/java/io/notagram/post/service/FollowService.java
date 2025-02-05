package io.notagram.post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.notagram.post.domain.entity.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class FollowService {
  @Value("${follow.service.url}")
  private String followServiceUrl;

  @Value("${internal.service.token}")
  private String internalToken;

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public FollowService(HttpClient httpClient) {
    this.httpClient = httpClient;
    this.objectMapper = new ObjectMapper();
  }

  public Boolean isFollowing(String followerId, String followeeId) {
    String url = followServiceUrl + "/api/follow/" + followeeId + "/follows/" + followerId;

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + internalToken)
            .GET()
            .build();
    try {
      // Send the request synchronously
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      log.debug("Received response with status code: {}", response.statusCode());
      if (response.statusCode() != 200) {
        throw new RuntimeException("Failed to check if is following: HTTP " + response.statusCode());
      }
      // Deserialize the JSON response into a UserDTO object
      return objectMapper.readValue(response.body(), Boolean.class);


    } catch (Exception e) {
      log.error("Error during HTTP request to identity service: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }

  }


}
