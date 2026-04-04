package io.github.pramudithalakshan.quickspringstarter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.PathMatcher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@org.springframework.boot.autoconfigure.AutoConfiguration
@AutoConfigureAfter({
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@EnableConfigurationProperties({QuickSecurityProperties.class, QuickStarterProperties.class})
@Slf4j
@RequiredArgsConstructor
public class AutoConfiguration {
    private final QuickStarterProperties quickStarterProperties;
    @PostConstruct
    public void validateConfiguration() {
        boolean noPublic = this.quickStarterProperties.getPublicPath().isEmpty();
        boolean noProtected = this.quickStarterProperties.getProtectedPath().isEmpty();

        if (noPublic && noProtected) {
            log.warn("QUICK STARTER NOTICE: No paths are defined!");
            log.warn("Because your configuration is empty, the library is");
            log.warn("protecting EVERYTHING by default (Deny-All).");
            log.warn("");
            log.warn("To allow access, please define paths in properties:");
            log.warn("quick.path.public-path=/your-public-endpoint/**");
        }
    }
    @PostConstruct
    private void welcomeMessage() {
        log.info("**********************************************************");
        log.info("Quick Spring Starter initialized successfully!");
        log.info("Security Module: Enabled");
        log.info("Documentation: https://github.com/Pramudithalakshan/quick-spring-starter.git");
        log.info("**********************************************************");
    }
    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, QuickStarterProperties quickStarterProperties,
                                                   JwtAuthenticationConverter converter, PathMatcher pathMatcher) throws Exception{
        return http
                .authorizeHttpRequests(auth ->{
                    quickStarterProperties.getPublicPath().forEach(path ->
                            auth.requestMatchers(path).permitAll());
                    quickStarterProperties.getProtectedPath().forEach((path, authority) -> {
                        boolean isConflict = quickStarterProperties.getPublicPath().stream()
                                .anyMatch(publicPath -> pathMatcher.match(publicPath, path));
                        if (isConflict) {
                            log.warn("Security Conflict: Protected path '{}' is covered by a Public pattern. It will be PUBLIC.", path);
                        } else {
                            auth.requestMatchers(path).hasAuthority(authority);
                        }
                    });
                 auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter))
                ).build();
    }
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder(QuickSecurityProperties properties) {
        String secret = properties.getJwtSecret();
        SecretKey spec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(spec).build();
    }
    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    public JwtAuthenticationConverter jwtAuthenticationConverter(QuickSecurityProperties quickSecurityProperties) {
        JwtGrantedAuthoritiesConverter listConverter = new JwtGrantedAuthoritiesConverter();
        listConverter.setAuthoritiesClaimName(quickSecurityProperties.getRoleClaimName());
        listConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(listConverter);
        return jwtAuthenticationConverter;
    }
}
