package io.github.pramudithalakshan.quickspringstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "quick.path")
@Getter
@Setter
public class QuickStarterProperties {
    private List<String> publicPath = new ArrayList<>();
    private Map<String,String> protectedPath = new HashMap<>();
}
