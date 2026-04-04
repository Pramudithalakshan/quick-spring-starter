# Quick Spring Starter

Quick Spring Starter is a Spring Boot auto-configuration module that wires JWT-based API security with minimal setup.

## What It Provides

- Auto-configured `SecurityFilterChain` (only when your app does not define one)
- Auto-configured `JwtDecoder` using an HMAC secret (`HmacSHA256`)
- Auto-configured `JwtAuthenticationConverter` with customizable authorities claim
- Configurable public and protected endpoint patterns
- Conflict handling and warning logs when an endpoint is configured as both public and protected

## Requirements

- Java 21+
- Spring Boot 4.x

## Installation

Add this dependency to your application:

```xml
<dependency>
  <groupId>io.github.pramudithalakshan</groupId>
  <artifactId>quick-spring-starter</artifactId>
  <version>1.1.0</version>
</dependency>
```

## Configuration

### Required properties

- `quick.security.jwt-secret`
- `quick.path.public-path`
- `quick.path.protected-path`

If any required property is missing or empty, startup fails with validation errors.

### Optional properties

- `quick.security.role-claim-name` (default: `roles`)

### YAML example

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

### Properties example

```properties
quick.security.jwt-secret=replace-with-a-strong-secret
quick.security.role-claim-name=roles

quick.path.public-path=/public/**, /actuator/health

quick.path.protected-path[/admin/**]=ROLE_ADMIN
quick.path.protected-path[/user/**]=ROLE_USER
```

## Security Rules

- Every `quick.path.public-path` entry is configured as `permitAll()`
- Every `quick.path.protected-path` entry is configured as `hasAuthority(...)`
- Any remaining endpoint requires authentication (`anyRequest().authenticated()`)
- If the same path appears in both lists, the public rule wins and a warning is logged

## JWT Authority Mapping

- The JWT decoder uses `quick.security.jwt-secret` with algorithm `HmacSHA256`
- Authorities are read from claim `quick.security.role-claim-name`
- No authority prefix is added, so JWT claim values must exactly match required authorities

Example JWT claim payload:

```json
{
  "sub": "user-123",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

## Auto-Configuration and Overrides

This starter registers:

- `SecurityFilterChain`
- `JwtDecoder`
- `JwtAuthenticationConverter`

Back-off behavior:

- If your app provides `SecurityFilterChain`, starter security chain is skipped
- If your app provides `JwtDecoder`, starter decoder is skipped
- If your app provides `JwtAuthenticationConverter`, starter converter is skipped

This makes it easy to start quickly and still replace parts with custom security logic when needed.

## Project Information

- Group: `io.github.pramudithalakshan`
- Artifact: `quick-spring-starter`
- Source: https://github.com/Pramudithalakshan/quick-spring-starter
- License: Apache License 2.0
