package com.example.my_book_shop_app.repositories;

import java.util.List;
import java.util.Optional;
import com.example.my_book_shop_app.data.BookViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookViewStatsRepository extends JpaRepository<BookViewStats, Long> {

    Optional<BookViewStats> findByBookId(Long bookId);

    @Query("SELECT b FROM BookViewStats b ORDER BY b.views DESC")
    List<BookViewStats> findTopN(@Param("limit") int limit);
}

