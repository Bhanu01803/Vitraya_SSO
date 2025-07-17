package org.example.vitraya_sso_token.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Add all frontend origins that will access the backend
                .allowedOrigins(
                    "http://localhost:4200", // Angular dev server
                    "http://localhost:8081", // (if used)
                    "http://localhost.vitrayatech.com",
                        "http://localhost:8080"// Custom local domain
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // Required for cookies to work cross-origin
                .maxAge(3600);
    }
}