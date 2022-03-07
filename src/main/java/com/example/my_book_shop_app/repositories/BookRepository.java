package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findAllByIsBestseller(boolean is);

    List<Book> findAllByPubDateGreaterThan(Date date);
}
