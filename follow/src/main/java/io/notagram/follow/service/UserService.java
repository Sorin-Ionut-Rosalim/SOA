package io.notagram.follow.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.notagram.follow.domain.entity.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class UserService {

  @Value("${identity.service.url}")
  private String identityServiceUrl;

  @Value("${internal.service.token}")
  private String internalToken;

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  // Inject the HttpClient bean (or create a new one if you prefer)
  public UserService(HttpClient httpClient) {
    this.httpClient = httpClient;
    this.objectMapper = new ObjectMapper();
  }

  public UserDTO findUserByUsername(String userId) {
    String url = identityServiceUrl + "/api/user/" + userId;
    log.debug("Making request to identity service: GET {}", url);

    // Build the GET request and add the Authorization header
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
        throw new RuntimeException("Failed to get user info: HTTP " + response.statusCode());
      }
      // Deserialize the JSON response into a UserDTO object
      UserDTO userDTO = objectMapper.readValue(response.body(), UserDTO.class);
      log.debug("Received user info for {}: {}", userId, userDTO);
      return userDTO;
    } catch (Exception e) {
      log.error("Error during HTTP request to identity service: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}
