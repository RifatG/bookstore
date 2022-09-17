package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.viewed.ViewedBook2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewedBook2UserRepository extends JpaRepository<ViewedBook2UserEntity, Integer> {

    boolean existsViewedBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId);

    ViewedBook2UserEntity findViewedBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId);
}
