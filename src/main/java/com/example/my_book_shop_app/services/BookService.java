package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooksData() {
        return bookRepository.findAll();
    }
    public List<Book> getPostponedBooksData() {
        return bookRepository.findAll();
    }

    public List<Book> getReservedBooksData() {
        return bookRepository.findAll();
    }

    public List<Book> getPopularBooksData() {
        return bookRepository.findAllByIsBestseller(true);
    }

    public List<Book> getRecentBooksData() {
        Instant date = Instant.now().minus(Duration.ofDays(10));
        return bookRepository.findAllByPubDateGreaterThan(Date.from(date));
    }

    public List<Book> getRecommendedBooksData() {
        return bookRepository.findAll();
    }
}