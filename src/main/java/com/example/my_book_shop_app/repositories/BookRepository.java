package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findAllByIsBestseller(byte is);

    List<Book> findAllByPubDateGreaterThan(Date date);

    List<Book> findBooksByAuthorNameContaining(String authorName);

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceBetween(int min, int max);

    List<Book> findBooksByPriceIs(int price);

    @Query(value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books)", nativeQuery = true)
    List<Book> getBookWithMaxDiscount();

    @Query(value = "SELECT * FROM books WHERE price = (SELECT MAX(price) FROM books)", nativeQuery = true)
    List<Book> getBookWithMaxPrice();

    @Query("from Book where isBestseller = 1")
    List<Book> getBestsellers();

    Page<Book> findBooksByTitleContaining(String bookTitle, Pageable nextPage);

    Page<Book> findAllByIsBestsellerEquals(byte is, Pageable nextPage);

    Page<Book> findAllByPubDateGreaterThan(Date date, Pageable nextPage);
}
