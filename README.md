# Quick Spring Starter

Quick Spring Starter is a Spring Boot auto-configuration module that sets up JWT-based API security with minimal boilerplate.

## Features

- Auto-registers a `SecurityFilterChain` (when missing in the consumer app)
- Auto-registers a `JwtDecoder` backed by an HMAC secret (when missing in the consumer app)
- Supports configurable public and protected endpoint patterns
- Supports configurable JWT roles claim name
- Uses Spring Boot auto-configuration imports (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`)

## Requirements

- Java 21+
- Spring Boot 4.x application

## Installation

Add the dependency in your consuming service:

```xml
<dependency>
  <groupId>io.github.pramudithalakshan</groupId>
  <artifactId>quick-spring-starter</artifactId>
  <version>1.1.0</version>
</dependency>
```

## Configuration

The following properties are required by the starter:

- `quick.security.jwt-secret`
- `quick.path.public-path`
- `quick.path.protected-path`

Optional property:

- `quick.security.role-claim-name` (default: `roles`)

Example configuration:
```properties
quick.path.public-path=/api/**
quick.path.protected-path[/api/**]=ROLE_ADMIN
```

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

If required properties are missing or empty, application startup fails with validation errors.

## Security Behavior

- Every pattern in `quick.path.public-path` is configured as `permitAll()`
- Every entry in `quick.path.protected-path` is configured as `hasAuthority(...)`
- All other endpoints require authentication (`anyRequest().authenticated()`)
- If a path is configured as both public and protected, the public rule wins and a warning is logged

## JWT Notes

- `JwtDecoder` uses `HmacSHA256` with `quick.security.jwt-secret`
- Authorities are read from the claim defined by `quick.security.role-claim-name`
- No authority prefix is added (`""`), so your JWT claim values should match the expected authorities exactly

## Override Behavior

The starter backs off when your app defines these beans:

- `SecurityFilterChain`
- `JwtDecoder`

This allows easy opt-out for advanced custom security setups.

## Project

- Group: `io.github.pramudithalakshan`
- Artifact: `quick-spring-starter`
- Source: https://github.com/Pramudithalakshan/quick-spring-starter
- License: Apache License 2.0
