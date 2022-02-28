package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.data.Author;
import com.example.my_book_shop_app.data.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books",(ResultSet rs, int rowNum) ->
                createBookFromResultOfQuery(rs));
        return new ArrayList<>(books);
    }

    public List<Book> getPostponedBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM postponed_books INNER JOIN books ON book_id = books.id",(ResultSet rs, int rowNum) ->
                createBookFromResultOfQuery(rs));
        return new ArrayList<>(books);
    }

    public List<Book> getReservedBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM reserved_books INNER JOIN books ON book_id = books.id",(ResultSet rs, int rowNum) ->
                createBookFromResultOfQuery(rs));
        return new ArrayList<>(books);
    }

    public List<Book> getPopularBooks() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM popular_books INNER JOIN books ON book_id = books.id",(ResultSet rs, int rowNum) ->
                createBookFromResultOfQuery(rs));
        return new ArrayList<>(books);
    }

    public List<Book> getRecentBooks() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM recent_books INNER JOIN books ON book_id = books.id",(ResultSet rs, int rowNum) ->
                createBookFromResultOfQuery(rs));
        return new ArrayList<>(books);
    }

    public List<Book> getRecommendedBooks() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM recommended_books INNER JOIN books ON book_id = books.id",(ResultSet rs, int rowNum) ->
                createBookFromResultOfQuery(rs));
        return new ArrayList<>(books);
    }

    private Book createBookFromResultOfQuery(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setAuthor(getAuthorByBookId(rs.getInt("author_id")));
        book.setTitle(rs.getString("title"));
        book.setPriceOld(rs.getInt("price_old"));
        book.setPrice(rs.getInt("price"));
        return book;
    }

    private String getAuthorByBookId(int authorId) {
        List<Author> authors = jdbcTemplate.query("SELECT * FROM authors WHERE authors.id=" + authorId, (ResultSet rs,
        int rowNum) -> {
            Author author = new Author();
            author.setId(rs.getInt("id"));
            author.setFirstName(rs.getString("first_name"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            return author;
        });
        return authors.get(0).toString();
    }
}
