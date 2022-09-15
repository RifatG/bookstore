package com.example.my_book_shop_app.data.request;

import lombok.Data;

@Data
public class FeedbackPayload {

    private String name;
    private String mail;
    private String subject;
    private String text;
}
