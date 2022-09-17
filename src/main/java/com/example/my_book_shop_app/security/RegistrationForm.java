package com.example.my_book_shop_app.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
