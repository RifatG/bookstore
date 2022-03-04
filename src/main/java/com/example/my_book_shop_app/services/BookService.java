package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.data.Book;
import com.example.my_book_shop_app.data.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getPostponedBooksData() {
        return bookRepository.findAllById(bookRepository.findAllFromPostponedBooks());
    }

    public List<Book> getReservedBooksData() {
        return bookRepository.findAllById(bookRepository.findAllFromReservedBooks());
    }

    public List<Book> getPopularBooks() {
        return bookRepository.findAllById(bookRepository.findAllFromPopularBooks());
    }

    public List<Book> getRecentBooks() {
        return bookRepository.findAllById(bookRepository.findAllFromRecentBooks());
    }

    public List<Book> getRecommendedBooks() {
        return bookRepository.findAllById(bookRepository.findAllFromRecommendedBooks());
    }
}
