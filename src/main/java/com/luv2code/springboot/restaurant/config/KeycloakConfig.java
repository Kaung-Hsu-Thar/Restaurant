//package com.luv2code.springboot.restaurant.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.beans.factory.annotation.Value;
//
//@Configuration
//public class KeycloakConfig {
//
//    // Replace with your Keycloak's issuer URI (Keycloak's Realm endpoint)
//    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
//    private String issuerUri;
//
//    private static final String[] AUTH_WHITELIST = {
//            "/v2/api-docs",
//            "/swagger-resources",
//            "/swagger-resources/**",
//            "/configuration/ui",
//            "/configuration/security",
//            "/swagger-ui.html",
//            "/webjars/**",
//            "/v3/api-docs/**",
//            "/swagger-ui/**",
//            "/public/**",
//            "/login",
//            "/test/**"
//    };
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        .requestMatchers("/staffs/**").hasRole("ADMIN")
//                        .requestMatchers("/user").hasAnyRole("ADMIN", "USER")
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//
//        return http.build();
//    }
//
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        // Configure the JWT decoder with Keycloak's issuer URI
//        return NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
//    }
//}
