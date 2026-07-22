package com.phatakp.kpevents.common.auth.config;

import com.phatakp.kpevents.common.auth.filter.ClerkJwtAuthFilter;
import com.phatakp.kpevents.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class WebSecurityConfig {

    private final ClerkJwtAuthFilter clerkJwtAuthFilter;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private static final String[] publicRoutes = {
            "/swagger-ui/**", "/v3/api-docs/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicRoutes).permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/admin/config").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/members/committee/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/transactions/donation/stats/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/transactions/balances/committee/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/items/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/transactions/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole(UserRole.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(clerkJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        // Handle missing credentials
                        .authenticationEntryPoint(new CustomAuthEntryPoint())
                        // Handle insufficient roles/permissions
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(configurationSource());
    }


    UrlBasedCorsConfigurationSource configurationSource() {
        List<String> allowedOrigins = Arrays.asList(frontendUrl.split(","));
        log.info("Allowed origins: {}", allowedOrigins);
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
