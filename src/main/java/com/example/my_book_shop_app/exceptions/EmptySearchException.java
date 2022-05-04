package com.example.my_book_shop_app.exceptions;

public class EmptySearchException extends Exception {
    public EmptySearchException(String errorMessage) {
        super(errorMessage);
    }
}
