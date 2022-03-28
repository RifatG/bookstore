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

    Page<Book> findAllByPubDateBetween(Date min, Date max, Pageable nextPage);

    @Query(value = "SELECT r.id, r.pub_date, r.is_bestseller, r.slug, r.title, r.image, r.description, r.price, r.discount, r.author_id FROM (SELECT b.*, COUNT(CASE WHEN b2ut.code='PAID' then 1 ELSE NULL END) AS paid, COUNT(CASE WHEN b2ut.code='CART' then 1 ELSE NULL END) * 0.7 AS cart, COUNT(CASE WHEN b2ut.code='KEPT' then 1 ELSE NULL END) * 0.4 AS kept FROM books AS b JOIN book2user AS b2u ON b.id=b2u.book_id JOIN book2user_type AS b2ut ON b2u.type_id=b2ut.id GROUP BY b.id) AS r WHERE (r.paid + r.cart + r.kept) >= :rating OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Book> getPopularBooks(double rating, int offset, int limit);

    Page<Book> findAllByIdIn(List<Integer> idList, Pageable nextPage);

    Page<Book> findAllByAuthorId(Integer id, Pageable nextPage);
}
