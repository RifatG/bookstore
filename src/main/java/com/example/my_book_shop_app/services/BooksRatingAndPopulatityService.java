package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BooksRatingAndPopulatityService {

    private final BookRepository bookRepository;

    @Autowired
    public BooksRatingAndPopulatityService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.getPopularBooks(nextPage);
    }
}
