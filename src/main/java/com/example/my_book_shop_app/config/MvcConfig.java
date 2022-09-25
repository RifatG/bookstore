package com.example.my_book_shop_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path.book-covers}")
    String bookImageUploadPath;

    @Value("${upload.path.author-covers}")
    String authorImageUploadPath;
    @Value("${server.root.path}")
    String rootPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Paths.get(bookImageUploadPath, "**").toString()).addResourceLocations("file:" + rootPath + bookImageUploadPath + "/");
        registry.addResourceHandler(Paths.get(authorImageUploadPath, "**").toString()).addResourceLocations("file:" + rootPath + authorImageUploadPath + "/");
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
