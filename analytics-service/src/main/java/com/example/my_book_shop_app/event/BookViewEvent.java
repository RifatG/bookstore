package com.example.my_book_shop_app.event;

import java.time.Instant;

public record BookViewEvent(
        Long userId,
        Long bookId,
        String bookSlug,
        String bookTitle,
        Instant viewedAt
) {}