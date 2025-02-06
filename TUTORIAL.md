# JWT Authentication Tutorial

This document explains how **JWT-based** authentication is configured in the project using the `JwtAuthenticationFilter`. We cover:

1. [Overview](#1-overview)
2. [Key Components](#2-key-components)
   - [Constructor & Key Setup](#21-constructor--key-setup)
   - [Skipping Filter on Certain Paths](#22-skipping-filter-on-certain-paths)
   - [Handling Internal Service Tokens](#23-handling-internal-service-tokens)
   - [Cookie-Based JWT Parsing](#24-cookie-based-jwt-parsing)
3. [Spring Security Integration](#3-spring-security-integration)
4. [Usage Flow](#4-usage-flow)
5. [Best Practices](#5-best-practices)

---

## 1. Overview

Our system uses a **stateless** JWT approach to authenticate **user requests**. Additionally, we allow **internal microservice** calls through a special token sent in the `Authorization` header.

- **User flow**: The browser stores the JWT in a cookie named `notagram-auth-token`. On each subsequent request, the filter checks this cookie, validates the token, and sets the authenticated user in Spring Security’s context.
- **Internal flow**: Certain microservices can call our endpoints using a special “internal service token.” If the token matches the expected value, the request is **auto-authorized** (bypassing cookie checks).

See [`JwtAuthenticationFilter.java`](./path/to/JwtAuthenticationFilter.java) for the exact implementation.

---

## 2. Key Components

### 2.1 Constructor & Key Setup

```java
public JwtAuthenticationFilter(String secretKey, String internalServiceToken) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    this.internalServiceToken = internalServiceToken;
    log.debug("Initialized JwtAuthenticationFilter with provided secret key");
}
```
- We **decode** the provided secretKey into an HMAC-SHA signing key (via the JJWT library).
- We store the internalServiceToken to identify **microservice-to-microservice** requests.

### 2.2 Constructor & Key Setup

```java
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    log.debug("JwtAuthenticationFilter checking URI: {}", path);
    return path.startsWith("/public")
            || path.startsWith("/api/auth")
            || path.startsWith("/error")
            || path.startsWith("/api/session");
}
```

- Any request whose URI begins with /public, /api/auth, /error, or /api/session skips JWT checks.
- You can modify this list as needed for public or unauthenticated endpoints.

### 2.2 Constructor & Key Setup

```java
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    log.debug("JwtAuthenticationFilter checking URI: {}", path);
    return path.startsWith("/public")
            || path.startsWith("/api/auth")
            || path.startsWith("/error")
            || path.startsWith("/api/session");
}
```

- Any request whose URI begins with /public, /api/auth, /error, or /api/session skips JWT checks.
- You can modify this list as needed for public or unauthenticated endpoints.

### 2.3 Handling Internal Service Tokens

```java
String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    String token = authHeader.substring(7);
    if (token.equals(internalServiceToken)) {
        // Mark request as internal
        Principal principal = new Principal("internal");
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        return;
    }
}
```

- If the Authorization header contains Bearer <internalServiceToken>, we treat the call as internal and skip the usual cookie-based checks.

### 2.4 Cookie-Based JWT Parsing

```java
// 1) Check if cookies exist
if (Objects.isNull(request.getCookies())) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("couldn't find \"notagram-auth-token\" cookie");
    return;
}

// 2) Locate the 'notagram-auth-token' cookie
List<Cookie> cookies = List.of(request.getCookies());
Cookie authCookie = cookies.stream()
        .filter(cookie -> "notagram-auth-token".equals(cookie.getName()))
        .findAny()
        .orElse(null);

if (Objects.isNull(authCookie)) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("couldn't find \"notagram-auth-token\" cookie");
    return;
}

// 3) Parse and validate the JWT
String token = authCookie.getValue();
Claims claims;
try {
    claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
} catch (Exception e) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(e.getMessage());
    return;
}

// 4) Set authenticated user in SecurityContext
String username = claims.getSubject();
Principal principal = new Principal(username);
UsernamePasswordAuthenticationToken authenticationToken =
    new UsernamePasswordAuthenticationToken(principal, null, null);

SecurityContextHolder.getContext().setAuthentication(authenticationToken);
filterChain.doFilter(request, response);
```

- The token is fetched from a cookie named notagram-auth-token.
- If parsing succeeds, the request is authenticated with the username from the JWT’s subject.
- If parsing fails or the cookie is missing, we respond with 401 Unauthorized.

---

## 3. Spring Security Integration
In Spring Security, you typically configure this filter in a SecurityFilterChain bean, for example:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    return http
       .csrf().disable()
       .authorizeHttpRequests(auth -> auth
           .requestMatchers("/public/**", "/api/auth/**").permitAll()
           .anyRequest().authenticated()
       )
       .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
       .build();
}
```

- addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) ensures our filter executes before Spring’s standard UsernamePasswordAuthenticationFilter.
- Any endpoint not matched by permitAll() will require a valid JWT or a valid internal service token.

---

## 4. Usage Flow

1. **User Login** (at `/api/auth/login` or similar):  
   - On success, the server responds with a `Set-Cookie: notagram-auth-token=<JWT>`.

2. **Browser Stores Cookie** automatically.

3. **Subsequent Requests**:
   - The browser sends the `notagram-auth-token` cookie to the server.
   - `JwtAuthenticationFilter` parses and validates it.
   - If valid, the user is authenticated for that request.

4. **Internal Microservice Calls**:
   - Send `Authorization: Bearer <internalServiceToken>` in the header.
   - The filter recognizes it and sets the principal to `"internal"`.

---

## 5. Best Practices

- **HTTP-Only & Secure Cookies**:  
  Prevents JavaScript from reading or modifying the token, and enforces HTTPS-only transmission.

- **Rotate Secret Keys** or store them safely:  
  Use environment variables or a secrets manager instead of embedding them in code.

- **Token Expiry & Refresh**:  
  Consider short-lived tokens with a refresh mechanism. If a token is stolen, its short lifespan mitigates potential damage.

- **Role-Based Access**:  
  Extend JWT to include roles/authorities, then set them in the `UsernamePasswordAuthenticationToken` for finer-grained access control.

---

## Conclusion

By using `JwtAuthenticationFilter`, our application:

- **Skips** authentication for specific public routes or error endpoints.
- **Accepts** a special `internalServiceToken` for microservice-to-microservice calls.
- **Expects** a user’s JWT in the `notagram-auth-token` cookie, which is validated on every request.
- **Integrates** seamlessly with Spring Security via a simple filter chain configuration.

This design ensures a **stateless** system—no server-side sessions—and cleanly separates different authentication modes (internal vs. user). It also provides a solid foundation for scaling and maintaining security in a microservices environment.


