package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Author getAuthorById(int id);
}
