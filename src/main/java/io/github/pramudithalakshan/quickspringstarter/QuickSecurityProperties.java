package io.github.pramudithalakshan.quickspringstarter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "quick.security")
@Getter
@Setter
@Validated
public class QuickSecurityProperties {
    @NotEmpty(message = "Property 'quick.security.jwt-secret' must be configured in application.properties")
    @NotBlank(message = "Property 'quick.security.jwt-secret' must be configured in application.properties")
    private String jwtSecret;
    private String roleClaimName = "roles";
}
