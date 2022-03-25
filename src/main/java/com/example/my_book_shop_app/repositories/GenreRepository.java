package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {
    @Query("select g.bookId from Book2GenreEntity g where g.genreId=:genreId")
    List<Integer> getBookIdListByGenreId(int genreId);

    GenreEntity getGenreEntityById(int id);
}
