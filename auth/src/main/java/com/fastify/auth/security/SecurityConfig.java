package com.fastify.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
            var config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4200/", "http://localhost:4200/*"));
            config.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH"));
            config.setAllowedHeaders(
                    List.of("Access-Control-Allow-Origin", "Access-Control-Allow-Headers",
                            "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            return config;
        }));

        httpSecurity.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry
                    .requestMatchers(
                            "/auth/signup",
                            "/auth/login"
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated();
        });

        httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return httpSecurity.build();
    }

}
