package com.example.my_book_shop_app.data.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AuthorPayload {

    private String name;
    private String description;
    private MultipartFile photo;
}
