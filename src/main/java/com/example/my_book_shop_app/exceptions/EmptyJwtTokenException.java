package com.example.my_book_shop_app.exceptions;

public class EmptyJwtTokenException extends Exception {
    public EmptyJwtTokenException(String errorMessage) {
        super(errorMessage);
    }
}
