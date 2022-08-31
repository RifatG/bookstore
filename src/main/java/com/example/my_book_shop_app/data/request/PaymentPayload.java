package com.example.my_book_shop_app.data.request;

import lombok.Data;

@Data
public class PaymentPayload {
    private Double sum;
    private String time;
}
