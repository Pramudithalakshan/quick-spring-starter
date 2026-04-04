package io.github.pramudithalakshan.quickspringstarter;

import jakarta.annotation.PostConstruct;
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
public class AutoConfiguration {
    @PostConstruct
    public void validateConfiguration(QuickStarterProperties quickStarterProperties) {
        boolean noPublic = quickStarterProperties.getPublicPath().isEmpty();
        boolean noProtected = quickStarterProperties.getProtectedPath().isEmpty();

        if (noPublic && noProtected) {
            log.warn("🛑 QUICK STARTER NOTICE: No paths are defined!");
            log.warn("Because your configuration is empty, the library is");
            log.warn("protecting EVERYTHING by default (Deny-All).");
            log.warn("");
            log.warn("To allow access, please define paths in properties:");
            log.warn("quick.path.public-path=/your-public-endpoint/**");
        } else {
            log.info("🛡️ Quick Starter Security: {} public and {} protected paths mapped.",
                    quickStarterProperties.getPublicPath().size(),
                    quickStarterProperties.getProtectedPath().size());
        }
    }
    @PostConstruct
    private void welcomeMessage() {
        log.info("**********************************************************");
        log.info("🚀 Quick Spring Starter initialized successfully!");
        log.info("🛡️ Security Module: Enabled");
        log.info("🔗 Documentation: https://github.com/Pramudithalakshan/quick-spring-starter.git");
        log.info("**********************************************************");
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, QuickStarterProperties quickStarterProperties,
                                                   QuickSecurityProperties quickSecurityProperties, JwtAuthenticationConverter converter) {
        return http
                .authorizeHttpRequests(auth ->{
                    quickStarterProperties.getPublicPath().forEach(path ->
                            auth.requestMatchers(path).permitAll());
                    quickStarterProperties.getProtectedPath().forEach((path, authority) -> {
                        if (quickStarterProperties.getPublicPath().contains(path)) {
                            log.warn("Security Conflict: {} is defined in both public and protected lists. Defaulting to PUBLIC.", path);
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
