# quick-spring-starter

Spring Boot starter that auto-configures a basic security setup and JWT decoder.

## Add the dependency

```xml
<dependency>
  <groupId>io.github.pramudithalakshan</groupId>
  <artifactId>quick-spring-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Required configuration

You must provide a stable JWT secret in the consuming application:

```yaml
quick:
  security:
    jwt-secret: your-signing-secret
```

If `quick.security.jwt-secret` is missing, the application will fail fast during startup.

## Behavior

- Requests under `/public/**` are allowed without authentication.
- All other endpoints require authentication.
- A `JwtDecoder` bean is created from `quick.security.jwt-secret` unless the consuming app already defines one.
