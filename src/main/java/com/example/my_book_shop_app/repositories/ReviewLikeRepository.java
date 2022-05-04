package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {
}
