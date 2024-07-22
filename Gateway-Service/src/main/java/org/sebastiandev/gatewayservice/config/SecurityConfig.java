package org.sebastiandev.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * This method is used to configure the security of the application.
     * @param http
     * @return
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/eureka/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults()));
        return http.build();
    }
    /**
     * This method is used to configure the JWT decoder
     * @return
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Replace "issuer-uri" with your actual issuer URI
        String issuerUri = "http://localhost:8181/realms/spring-boot-microservices-realm";
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri);
    }

}
