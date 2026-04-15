# Quick Spring Starter

Quick Spring Starter is a Spring Boot auto-configuration library for JWT-protected APIs. It provides a preconfigured OAuth2 Resource Server setup with path-based authorization, while still allowing full override from your application.

Note: Version 1.1.0 was published accidentally and contains configuration bugs. Please use v1.1.1 or higher.

## What's New in v1.2.0

- Added built-in `JwtService` for generating HS256 JWT tokens in your application code
- Improved default path property handling with safe empty collections
- Improved startup guidance with clearer logs when path rules are not configured
- Kept full Spring Boot back-off behavior so application-defined beans still take priority

## Features

- Auto-configured `SecurityFilterChain` (when your app does not define one)
- Auto-configured `JwtDecoder` using an HMAC secret (`HmacSHA256`)
- Auto-configured `JwtAuthenticationConverter` with configurable claim name
- Auto-configured `JwtService` bean for creating signed JWT tokens
- Public and protected endpoint rules from external configuration
- Conflict warning logs when the same endpoint is effectively both public and protected

## Requirements

- Java 21+
- Spring Boot 4.x

## Installation

Add the dependency to your application:

```xml
<dependency>
  <groupId>io.github.pramudithalakshan</groupId>
  <artifactId>quick-spring-starter</artifactId>
  <version>1.2.0</version>
</dependency>
```

## Quick Start

1. Add the dependency.
2. Set `quick.security.jwt-secret`.
3. Configure both `quick.path.public-path` and `quick.path.protected-path`.

Minimal `application.yml`:

```yaml
quick:
  security:
    jwt-secret: replace-with-a-strong-secret
    role-claim-name: roles
  path:
    public-path:
      - /public/**
      - /actuator/health
    protected-path:
      /admin/**: ROLE_ADMIN
      /user/**: ROLE_USER
```

Equivalent `application.properties`:

```properties
quick.security.jwt-secret=replace-with-a-strong-secret
quick.security.role-claim-name=roles

quick.path.public-path=/public/**,/actuator/health
quick.path.protected-path[/admin/**]=ROLE_ADMIN
quick.path.protected-path[/user/**]=ROLE_USER
```

## Configuration Reference

| Property | Required | Default | Description |
| --- |----------| --- | --- |
| `quick.security.jwt-secret` | Yes      | - | Secret used for HMAC JWT verification (`HmacSHA256`). |
| `quick.security.role-claim-name` | No       | `roles` | JWT claim name used to extract authorities. |
| `quick.path.public-path` | No       | - | List of request matchers configured as `permitAll()`. |
| `quick.path.protected-path` | No       | - | Map of request matcher pattern to required authority. |

## Security Behavior

- Each `quick.path.public-path` pattern is configured with `permitAll()`.
- Each `quick.path.protected-path` entry is configured with `hasAuthority(...)`.
- All other requests require authentication (`anyRequest().authenticated()`).
- If a protected pattern is covered by a public pattern, the public rule is applied and a warning is logged.
- If both path collections are present but empty, the starter logs a warning and still applies `anyRequest().authenticated()`.

## JWT Authority Mapping

- Authorities are read from `quick.security.role-claim-name`.
- The starter uses no authority prefix.
- Token claim values must exactly match configured authorities (for example `ROLE_ADMIN`).

Example JWT payload:

```json
{
  "sub": "user-123",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

## Auto-Configuration and Override Behavior

The starter contributes:

- `SecurityFilterChain`
- `JwtDecoder`
- `JwtAuthenticationConverter`
- `JwtService`

Back-off behavior:
- If your app defines `SecurityFilterChain`, starter chain configuration is skipped.
- If your app defines `JwtDecoder`, starter decoder is skipped.
- If your app defines `JwtAuthenticationConverter`, starter converter is skipped.
- If your app defines `JwtService`, starter token service is skipped.

## JwtService Usage

You can inject `JwtService` to generate signed tokens using your configured `quick.security.jwt-secret`.

```java
import io.github.pramudithalakshan.quickspringstarter.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class TokenController {
  private final JwtService jwtService;

  TokenController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @GetMapping("/token")
  String token() throws Exception {
    return jwtService.generateToken("user-123", List.of("ROLE_USER"));
  }
}
```

## Expected Startup Outcomes

- If `quick.security.jwt-secret` is missing, startup fails fast due to validation.
- If both path collections are configured and empty, startup succeeds and all routes still require authentication.
- If path collections are omitted entirely, starter defaults to empty collections and still secures all routes by default.

## Troubleshooting

- Startup fails with missing secret:
  Configure `quick.security.jwt-secret`.
- Startup fails with null path collections:
  Not expected in v1.2.0. Path properties default to empty collections.
- Endpoint unexpectedly public:
  Check for overlap where a public ant-style pattern covers a protected path.

## Project Information

- Group: `io.github.pramudithalakshan`
- Artifact: `quick-spring-starter`
- Source: https://github.com/Pramudithalakshan/quick-spring-starter
- License: Apache License 2.0
