package io.github.pramudithalakshan.quickspringstarter;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "quick.security")
@Getter
@Setter
@Validated
// The JWT secret is required; the claim name stays configurable for different token formats.
public class QuickSecurityProperties {
    @NotBlank(message = "message = \"Property 'quick.security.jwt-secret' must be configured in application configuration\"")
    private String jwtSecret;
    private String roleClaimName = "roles";
}
