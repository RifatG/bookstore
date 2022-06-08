package com.example.my_book_shop_app.exceptions;

public class JwtInBlacklistException extends Exception {

    public JwtInBlacklistException(String errorMessage) {
        super(errorMessage);
    }
}
