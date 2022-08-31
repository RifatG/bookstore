package com.example.my_book_shop_app.data;

import lombok.Data;

@Data
public class PaymentResponse {

    private boolean result;
    private String url;
    private String error;

    public PaymentResponse(String url) {
        this.result = true;
        this.url = url;
    }

    public PaymentResponse(boolean result, String error) {
        this.result = result;
        this.error = error;
    }
}
