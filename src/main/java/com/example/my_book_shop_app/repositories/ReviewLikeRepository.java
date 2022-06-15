package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {

    BookReviewLikeEntity findByUserAndReviewEntityAndValue(UserEntity user, BookReviewEntity reviewEntity, short value);
}
