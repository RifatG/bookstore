package com.example.my_book_shop_app.rest_controllers;

import com.example.my_book_shop_app.data.ApiResponse;
import com.example.my_book_shop_app.exceptions.BookStoreApiWrongParameterException;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BooksRestApiController {

    private final BookService bookService;

    @Autowired
    public BooksRestApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books/by-author")
    public ResponseEntity<List<Book>> booksByAuthor(@RequestParam("author") String authorName) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorName));
    }

    @GetMapping("/books/by-title")
    public ResponseEntity<ApiResponse<Book>> booksByTitle(@RequestParam("title") String title) throws BookStoreApiWrongParameterException {
        ApiResponse<Book> response = new ApiResponse();
        List<Book> books = bookService.getBooksByTitle(title);
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + books.size() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/by-price-range")
    public ResponseEntity<List<Book>> priceRangeBooks(@RequestParam("min") int min, @RequestParam("max") int max) {
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max));
    }

    @GetMapping("/books/by-price")
    public ResponseEntity<List<Book>> booksByPrice(@RequestParam("price") int price) {
        return ResponseEntity.ok(bookService.getBooksWithPrice(price));
    }

    @GetMapping("/books/with-max_price")
    public ResponseEntity<List<Book>> maxPriceBooks() {
        return ResponseEntity.ok(bookService.getBookWithMaxPrice());
    }

    @GetMapping("/books/with-max_discount")
    public ResponseEntity<List<Book>> maxDiscountBooks() {
        return ResponseEntity.ok(bookService.getBookWithMaxDiscount());
    }

    @GetMapping("/books/bestsellers")
    public ResponseEntity<List<Book>> bestsellerBooks() {
        return ResponseEntity.ok(bookService.getBestsellers());
    }

    @GetMapping("/books/recent_added")
    public ResponseEntity<List<Book>> recentBooks() {
        return ResponseEntity.ok(bookService.getRecentBooksData());
    }
}
