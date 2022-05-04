package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<BookReviewEntity, Integer> {

}
