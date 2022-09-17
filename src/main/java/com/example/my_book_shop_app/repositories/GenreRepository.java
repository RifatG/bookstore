package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {
    @Query("select g.bookId from Book2GenreEntity g where g.genreId=:genreId")
    List<Integer> getBookIdListByGenreId(int genreId);

    GenreEntity getGenreEntityById(int id);

    @Query(value = "select genre_id from (select b.id, user_id from books as b join book2user as b2u on b.id = b2u.book_id) as bu join book2genre as b2g on bu.id = b2g.book_id group by bu.user_id, genre_id having count(genre_id) > 3 and bu.user_id = :userId order by count(genre_id) desc", nativeQuery = true)
    Set<Integer> getGenreIdsByUserRelationsToBook(Integer userId);

    @Query(value = "select genre_id from (select b.id, user_id from books as b join viewed_book2user as vb on b.id = vb.book_id) as bu join book2genre as bg on bu.id = bg.book_id group by user_id, genre_id having count(genre_id) > 3 and user_id = 1 order by count(genre_id) desc", nativeQuery = true)
    Set<Integer> getGenreIdsOfViewedBooksByUser(Integer userId);
}
