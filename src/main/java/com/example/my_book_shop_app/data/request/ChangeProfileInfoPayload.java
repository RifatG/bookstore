package com.example.my_book_shop_app.data.request;

import lombok.Data;

@Data
public class ChangeProfileInfoPayload {

    private String name;
    private String mail;
    private String phone;
    private String password;
    private String passwordReply;
}
