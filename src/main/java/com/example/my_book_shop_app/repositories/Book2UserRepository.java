package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    boolean existsBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId);

    Book2UserEntity findBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId);
}
