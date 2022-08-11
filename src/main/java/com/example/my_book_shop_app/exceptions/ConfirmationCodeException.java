package com.example.my_book_shop_app.exceptions;

public class ConfirmationCodeException extends Exception {
    public ConfirmationCodeException(String errorMessage) {
        super(errorMessage);
    }
}
