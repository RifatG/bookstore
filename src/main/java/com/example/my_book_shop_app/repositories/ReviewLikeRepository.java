package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {

    BookReviewLikeEntity findByUserAndReviewEntityAndValue(UserEntity user, BookReviewEntity reviewEntity, short value);

    List<BookReviewLikeEntity> findAllByReviewEntity(BookReviewEntity review);

    @Query("select count(l.value) from BookReviewEntity r join BookReviewLikeEntity l on r.id = l.reviewEntity.id where l.value = 1 and r.user.id = :userId")
    long getLikesByUserId(Integer userId);

    @Query("select count(l.value) from BookReviewEntity r join BookReviewLikeEntity l on r.id = l.reviewEntity.id where l.value = -1 and r.user.id = :userId")
    long getDislikesByUserId(Integer userId);
}
