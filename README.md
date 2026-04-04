# Quick Spring Starter

Quick Spring Starter is a Spring Boot auto-configuration library for JWT-protected APIs. It provides sensible security defaults while still allowing full override in your application.

## Features

- Auto-configured `SecurityFilterChain` (only if your app does not define one)
- Auto-configured `JwtDecoder` using an HMAC secret (`HmacSHA256`)
- Auto-configured `JwtAuthenticationConverter` with customizable authorities claim
- Public and protected endpoint pattern mapping from configuration
- Conflict detection with warning logs when a path is both public and protected

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
2. Configure public and protected paths.
3. Provide a strong `quick.security.jwt-secret`.

Minimal YAML:

```yaml
quick:
  security:
    jwt-secret: replace-with-a-strong-secret
  path:
    public-path:
      - /public/**
      - /actuator/health
    protected-path:
      /admin/**: ROLE_ADMIN
      /user/**: ROLE_USER
```

## Configuration Reference

| Property | Required | Default | Description |
| --- | --- | --- | --- |
| `quick.security.jwt-secret` | Yes | - | HMAC secret used by JWT decoder (`HmacSHA256`). |
| `quick.security.role-claim-name` | No | `roles` | JWT claim name that contains authorities/roles. |
| `quick.path.public-path` | Yes | - | List of endpoint patterns that are exposed with `permitAll()`. |
| `quick.path.protected-path` | Yes | - | Map of endpoint pattern to required authority. |

If required properties are missing or empty, startup fails with validation errors.

## Properties File Example

```properties
quick.security.jwt-secret=replace-with-a-strong-secret
quick.security.role-claim-name=roles

quick.path.public-path=/public/**, /actuator/health

quick.path.protected-path[/admin/**]=ROLE_ADMIN
quick.path.protected-path[/user/**]=ROLE_USER
```

## Security Behavior

- Every `quick.path.public-path` entry is configured as `permitAll()`.
- Every `quick.path.protected-path` entry is configured as `hasAuthority(...)`.
- Any unmatched endpoint requires authentication (`anyRequest().authenticated()`).
- If a path appears in both public and protected settings, the public rule wins and a warning is logged.

## JWT Authority Mapping

- Authorities are read from claim `quick.security.role-claim-name`.
- No authority prefix is added by the starter.
- Claim values must match configured authorities exactly (for example `ROLE_ADMIN`).

Example JWT payload:

```json
{
  "sub": "user-123",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

## Auto-Configuration and Overrides

The starter registers:

- `SecurityFilterChain`
- `JwtDecoder`
- `JwtAuthenticationConverter`

Back-off behavior:

- If your app provides `SecurityFilterChain`, starter security chain is skipped.
- If your app provides `JwtDecoder`, starter decoder is skipped.
- If your app provides `JwtAuthenticationConverter`, starter converter is skipped.

This lets you start quickly and selectively replace components as your security needs evolve.

## Project Information

- Group: `io.github.pramudithalakshan`
- Artifact: `quick-spring-starter`
- Source: https://github.com/Pramudithalakshan/quick-spring-starter
- License: Apache License 2.0
