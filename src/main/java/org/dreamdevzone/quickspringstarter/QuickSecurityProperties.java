package org.dreamdevzone.quickspringstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "quick.security")
@Getter
@Setter
public class QuickSecurityProperties {
    private String jwtSecret;
}
