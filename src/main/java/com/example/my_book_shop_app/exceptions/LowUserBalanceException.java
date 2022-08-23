package com.example.my_book_shop_app.exceptions;

public class LowUserBalanceException extends Exception {
    public LowUserBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
