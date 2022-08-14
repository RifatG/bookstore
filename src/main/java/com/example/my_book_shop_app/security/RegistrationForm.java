package com.example.my_book_shop_app.security;

import lombok.Data;

@Data
public class RegistrationForm {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
