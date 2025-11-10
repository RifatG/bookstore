package com.example.my_book_shop_app.rest_controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class MainPageRestApiController {

    private final BookService bookService;

    @Autowired
    public MainPageRestApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books/recommended")
    public BooksPageDto getRecommendedBooksPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }

    @GetMapping("/books/recent")
    public BooksPageDto getRecentBooksPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecentBooks(offset, limit).getContent());
    }

    @GetMapping("/books/popular")
    public BooksPageDto getPopularBooksPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }
}
