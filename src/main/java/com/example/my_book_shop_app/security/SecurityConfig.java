package com.example.my_book_shop_app.security;

import com.example.my_book_shop_app.security.jwt.JWTRequestFilter;
import com.example.my_book_shop_app.services.CookieHandler;
import com.example.my_book_shop_app.services.JwtBlacklistService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String SIGN_IN_URL = "/signin";
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTRequestFilter filter;
    private final CookieHandler cookieHandler;
    private final JwtBlacklistService jwtBlacklistService;
    private final OauthAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService,
                          JWTRequestFilter filter,
                          CookieHandler cookieHandler,
                          JwtBlacklistService jwtBlacklistService,
                          OauthAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.filter = filter;
        this.cookieHandler = cookieHandler;
        this.jwtBlacklistService = jwtBlacklistService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8081",
                "http://localhost:8082",  // ← ДОБАВЬТЕ ЭТОТ ПОРТ
                "http://app:8085",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ← ДОБАВЬТЕ ЭТУ СТРОКУ
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // ← И ЭТУ
                        .requestMatchers("/my", "/profile", "/books/viewed").authenticated()
                        .requestMatchers("/users", "/users/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage(SIGN_IN_URL)
                        .failureUrl(SIGN_IN_URL)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            String token = cookieHandler.getJwtTokenFromCookie(request);
                            if(token != null) jwtBlacklistService.addToBlacklist(token);
                        })
                        .logoutSuccessUrl(SIGN_IN_URL)
                        .deleteCookies("token")
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customAuthenticationSuccessHandler)
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}