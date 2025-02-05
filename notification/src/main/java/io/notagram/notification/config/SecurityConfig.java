package io.notagram.notification.config;


import io.notagram.notification.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

  @Value("${jwt.secret}")
  private String jwtSecret;
  @Value("${internal.service.token}")
  private String internalServiceToken;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
    return security
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers("/ws/**").permitAll()
                    .requestMatchers("/error").permitAll()
//                    .requestMatchers("/api/post/**").authenticated()
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(new JwtAuthenticationFilter(jwtSecret, internalServiceToken), UsernamePasswordAuthenticationFilter.class)
            .build();
  }
}
