package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getPostponedBooksData() {
        return bookRepository.findAll();
    }

    public List<Book> getReservedBooksData() {
        return bookRepository.findAll();
    }

    public List<Book> getPopularBooksData() {
        return bookRepository.getBestsellers();
    }

    public List<Book> getRecentBooksData() {
        Instant date = Instant.now().minus(Duration.ofDays(2 * 365));
        return bookRepository.findAllByPubDateGreaterThan(Date.from(date));
    }

    public List<Book> getRecommendedBooksData() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByAuthor(String authorName) {
        return bookRepository.findBooksByAuthorNameContaining(authorName);
    }

    public List<Book> getBooksByTitle(String bookTitle) {
        return bookRepository.findBooksByTitleContaining(bookTitle);
    }

    public List<Book> getBooksWithPriceBetween(int min, int max) {
        return bookRepository.findBooksByPriceBetween(min, max);
    }

    public List<Book> getBooksWithPrice(int price) {
        return bookRepository.findBooksByPriceIs(price);
    }

    public List<Book> getBookWithMaxDiscount() {
        return bookRepository.getBookWithMaxDiscount();
    }

    public List<Book> getBookWithMaxPrice() {
        return bookRepository.getBookWithMaxPrice();
    }

    public List<Book> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAll(nextPage);
    }

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAllByIsBestsellerEquals((byte) 1, nextPage);
    }

    public Page<Book> getPageOfRecentBooks(Integer offset, Integer limit) {
        Calendar max = new GregorianCalendar();
        Calendar min = new GregorianCalendar();
        min.set(Calendar.MONTH, max.get(Calendar.MONTH) - 1);
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by("pubDate"));
        return bookRepository.findAllByPubDateBetween(min.getTime(), max.getTime(), nextPage);
    }

    public Page<Book> getPageOfBooksByDateRange(Date min, Date max, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by("pubDate"));
        return bookRepository.findAllByPubDateBetween(min, max, nextPage);
    }

    public Page<Book> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTitleContaining(searchWord, nextPage);
    }
}
