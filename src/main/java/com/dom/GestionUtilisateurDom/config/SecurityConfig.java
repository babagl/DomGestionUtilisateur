package com.dom.gestionutilisateurdom.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public final JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.
                authorizeHttpRequests(auth-> auth
                        .requestMatchers(HttpMethod.GET,"/api/home").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/user/**").hasRole(USER)
                        .requestMatchers(HttpMethod.GET,"/api/admin/**").hasRole(ADMIN)
                        .anyRequest().authenticated())
                .sessionManagement(s-> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt-> jwt.jwtAuthenticationConverter(jwtConverter)))
        .build();
    }
}
