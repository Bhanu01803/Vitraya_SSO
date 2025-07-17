package org.example.vitraya_sso_token.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring SecurityFilterChain...");
        
        http
                .cors(cors -> {
                    System.out.println("Configuring CORS...");
                    cors.configurationSource(corsConfigurationSource());
                })
                .csrf(csrf -> {
                    System.out.println("Disabling CSRF...");
                    csrf.disable();
                })
                .authorizeHttpRequests(authz -> {
                    System.out.println("Configuring authorization...");
                    authz.requestMatchers(
                            "/api/v1/user/send-otp", 
                            "/api/v1/user/verify-otp", 
                            "/api/v1/user/check/session",
                            "/api/v1/sso/**",
                            "/sso/**",
                            "/error",
                            "/.well-known/**",
                            "/static/**",
                            "/css/**",
                            "/js/**",
                            "/images/**"
                        ).permitAll()
                        .anyRequest().permitAll(); // Allow all requests for now
                })
                .sessionManagement(session -> {
                    System.out.println("Configuring session management...");
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .securityContext(securityContext -> {
                    System.out.println("Configuring security context...");
                    securityContext.requireExplicitSave(false);
                })
                .headers(headers -> {
                    System.out.println("Configuring headers...");
                    headers.frameOptions().disable();
                })
                .anonymous(anonymous -> {
                    System.out.println("Configuring anonymous access...");
                    anonymous.disable();
                });

        System.out.println("SecurityFilterChain configuration completed.");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("Creating CORS configuration...");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://localhost:8081","http://localhost.vitrayatech.com", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight requests for 1 hour
        
        configuration.addAllowedHeader("X-Client-Platform");
        configuration.addAllowedHeader("partner_accessToken");
        configuration.addAllowedHeader("partner_refreshToken");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Content-Type");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        System.out.println("CORS configuration created successfully.");
        return source;
    }
}