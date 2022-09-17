package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.RecommendedBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendedBookRepository extends JpaRepository<RecommendedBook, Integer> {

    @Query("select rb.book from RecommendedBook rb where rb.userId = :userId order by rb.id desc")
    Page<Book> getAllRecommendedBooksByUserId(Integer userId, Pageable nextPage);

    boolean existsRecommendedBookByUserIdAndBook(Integer userId, Book book);
}
