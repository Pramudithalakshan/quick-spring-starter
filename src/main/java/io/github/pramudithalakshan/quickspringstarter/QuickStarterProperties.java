package io.github.pramudithalakshan.quickspringstarter;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "quick.path")
@Getter
@Setter
@Validated
public class QuickStarterProperties {
    @NotEmpty(message = "Path 'quick.path.public-path' must be configured in application properties")
    private List<String> publicPath;
    @NotEmpty(message = "Path 'quick.path.protected-path' must be configured in application properties")
    private Map<String,String> protectedPath;
}
