package com.example.my_book_shop_app.data.request;

import lombok.Data;

@Data
public class BookReviewRequest {

    private String bookId;
    private String text;
    private Integer value;
    private Integer reviewId;
}
