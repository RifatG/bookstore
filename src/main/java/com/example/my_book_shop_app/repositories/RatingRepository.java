package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Integer> {
}
