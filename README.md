# Quick Spring Starter

Quick Spring Starter provides opinionated Spring Security auto-configuration for JWT-based resource servers.

## What it does

- Registers a default `SecurityFilterChain`.
- Allows unauthenticated access to `/public/**`.
- Requires authentication for all other endpoints.
- Registers a `JwtDecoder` using a shared secret.
- Backs off automatically if your application already defines `SecurityFilterChain` or `JwtDecoder` beans.

## Requirements

- Java 21+
- Spring Boot application

## Installation

Add this dependency to your application:

```xml
<dependency>
  <groupId>io.github.pramudithalakshan</groupId>
  <artifactId>quick-spring-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Required configuration

Set a non-empty JWT secret in your application configuration:

```yaml
quick:
  security:
    jwt-secret: your-signing-secret
```

If `quick.security.jwt-secret` is missing or blank, startup fails with an error.

## Default security behavior

- `/public/**`: permitted without authentication
- Any other path: authentication required

## Customization

If you need custom behavior, define your own beans in the consuming app:

- Custom `SecurityFilterChain` to change authorization rules
- Custom `JwtDecoder` to use a different JWT validation strategy

When these beans are present, the starter default beans are not applied.
