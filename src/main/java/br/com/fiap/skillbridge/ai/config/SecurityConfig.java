package br.com.fiap.skillbridge.ai.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.headers(h -> h.frameOptions(f -> f.disable())); // H2
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**","/swagger-ui/**","/v3/api-docs/**","/actuator/**").permitAll()
                .anyRequest().permitAll()  // DEV: tudo liberado
        );
        return http.build();
    }
}
