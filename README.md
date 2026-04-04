# Quick Spring Starter

Quick Spring Starter is a Spring Boot auto-configuration library for JWT-protected APIs. It provides a preconfigured OAuth2 Resource Server setup with path-based authorization, while still allowing full override from your application.

## Features

- Auto-configured `SecurityFilterChain` (when your app does not define one)
- Auto-configured `JwtDecoder` using an HMAC secret (`HmacSHA256`)
- Auto-configured `JwtAuthenticationConverter` with configurable claim name
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
  <version>1.1.0</version>
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

Back-off behavior:
- If your app defines `SecurityFilterChain`, starter chain configuration is skipped.
- If your app defines `JwtDecoder`, starter decoder is skipped.
- If your app defines `JwtAuthenticationConverter`, starter converter is skipped.

## Expected Startup Outcomes

- If `quick.security.jwt-secret` is missing, startup fails fast due to validation.
- If both path collections are configured and empty, startup succeeds and all routes still require authentication.
- If path collections are omitted entirely, property binding can produce null collections and fail during initialization.

## Troubleshooting

- Startup fails with missing secret:
  Configure `quick.security.jwt-secret`.
- Startup fails with null path collections:
  Ensure both `quick.path.public-path` and `quick.path.protected-path` are present in configuration (they may be empty, but should be defined).
- Endpoint unexpectedly public:
  Check for overlap where a public ant-style pattern covers a protected path.

## Project Information

- Group: `io.github.pramudithalakshan`
- Artifact: `quick-spring-starter`
- Source: https://github.com/Pramudithalakshan/quick-spring-starter
- License: Apache License 2.0
