package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {

    BookReviewLikeEntity findByUserAndReviewEntityAndValue(UserEntity user, BookReviewEntity reviewEntity, short value);

    List<BookReviewLikeEntity> findAllByReviewEntity(BookReviewEntity review);
}
