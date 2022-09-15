package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author getAuthorById(int id);

    boolean existsAuthorByName(String name);

    Author getAuthorByName(String name);

    Author getAuthorBySlug(String slug);

    @Query("select b.author.id from Book as b join Book2UserEntity as b2u on b.id = b2u.bookId group by b2u.userId, b.author.id having count(b.author.id) > 2 and b2u.userId = :userId order by count(b.author.id) desc")
    Set<Integer> getAuthorIdsByUserRelationsToBook(Integer userId);

    @Query("select b.author.id from Book b join ViewedBook2UserEntity vb on b.id = vb.bookId group by vb.userId, b.author.id having count(b.author.id) > 2 and vb.userId = :userId order by count(b.author.id) desc")
    Set<Integer> getAuthorIdsOfViewedBooksByUser(Integer userId);
}
