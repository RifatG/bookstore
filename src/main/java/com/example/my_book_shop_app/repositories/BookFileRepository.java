package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.file.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFile, Integer> {

     public BookFile findBookFileByHash(String hash);
}
