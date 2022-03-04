package com.example.my_book_shop_app.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("select id from PopularBook")
    List<Integer> findAllFromPopularBooks();

    @Query("select id from PostponedBook")
    List<Integer> findAllFromPostponedBooks();

    @Query("select id from RecentBook")
    List<Integer> findAllFromRecentBooks();

    @Query("select id from RecommendedBook")
    List<Integer> findAllFromRecommendedBooks();

    @Query("select id from ReservedBook")
    List<Integer> findAllFromReservedBooks();
}
