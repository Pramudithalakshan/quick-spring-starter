package io.github.pramudithalakshan.quickspringstarter;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties({QuickSecurityProperties.class, QuickStarterProperties.class})
@Slf4j
@AutoConfigureAfter
public class AutoConfiguration {
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
                                                   QuickSecurityProperties quickSecurityProperties) {
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
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(quickSecurityProperties)))
                ).build();
    }
    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder(QuickSecurityProperties properties) {
        String secret = properties.getJwtSecret();
        SecretKey spec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(spec).build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(QuickSecurityProperties quickSecurityProperties) {
        JwtGrantedAuthoritiesConverter listConverter = new JwtGrantedAuthoritiesConverter();
        listConverter.setAuthoritiesClaimName(quickSecurityProperties.getRoleClaimName());
        listConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(listConverter);
        return jwtAuthenticationConverter;
    }
}
