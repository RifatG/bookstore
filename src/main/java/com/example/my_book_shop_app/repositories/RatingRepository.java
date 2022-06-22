package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Integer> {

    RatingEntity findRatingEntityByBookAndUser(Book book, UserEntity user);
}
