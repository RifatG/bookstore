package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    boolean existsBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId);

    boolean existsBook2UserEntityByUserIdAndBookIdAndTypeId(Integer userId, Integer bookId, Integer typeId);

    Book2UserEntity findBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId);

    @Query("select count(bu) from Book2UserEntity bu where bu.userId = :userId and bu.typeId = :typeId")
    long getCountByUserIdAndTypeId(Integer userId, Integer typeId);
}
