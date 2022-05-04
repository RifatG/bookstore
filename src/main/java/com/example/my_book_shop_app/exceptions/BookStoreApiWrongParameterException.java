package com.example.my_book_shop_app.exceptions;

public class BookStoreApiWrongParameterException extends Exception {
    public BookStoreApiWrongParameterException(String errorMessage) {
        super(errorMessage);
    }
}
