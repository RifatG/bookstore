package com.example.my_book_shop_app.struct.external_api;

import lombok.Data;

@Data
public class ConfirmationCodeResponse {
    private Integer code;
    private String status;
    private String status_text;
}
