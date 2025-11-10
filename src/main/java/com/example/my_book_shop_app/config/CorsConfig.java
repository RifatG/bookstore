package com.example.my_book_shop_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Разрешаем доступ с любых источников (для продакшена укажите конкретные)
        config.addAllowedOrigin("*");

        // Разрешаем все заголовки
        config.addAllowedHeader("*");

        // Разрешаем все HTTP-методы (GET, POST, PUT, DELETE и т.д.)
        config.addAllowedMethod("*");

        // Разрешаем передачу cookies/авторизации
        config.setAllowCredentials(false);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}