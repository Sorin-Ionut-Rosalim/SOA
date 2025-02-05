package service;

import domain.entity.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {
  @Value("${identity.service.url}")
  private String identityServiceUrl;
  private final RestTemplate restTemplate;


  public UserService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public UserDTO findUserByUsername(String username) {
    String url = String.format("%s/api/user/%s", identityServiceUrl, username);
    return restTemplate.getForObject(url, UserDTO.class);
  }
}
