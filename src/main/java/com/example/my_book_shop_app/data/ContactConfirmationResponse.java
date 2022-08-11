package com.example.my_book_shop_app.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ContactConfirmationResponse {

    private Boolean result;
    private String token;
    private String error;

    public ContactConfirmationResponse(Boolean result) {
        this.result = result;
    }

    public ContactConfirmationResponse(Boolean result, String token) {
        this.result = result;
        this.token = token;
    }

    public ContactConfirmationResponse(String error) {
        this.result = false;
        this.error = error;
    }
}
