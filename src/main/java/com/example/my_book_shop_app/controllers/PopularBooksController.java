package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class PopularBooksController {

    private BookService bookService;

    @Autowired
    public PopularBooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("booksData")
    public List<Book> popularBooksAttribute() {
        return bookService.getPopularBooksData();
    }

    @GetMapping("/books/popular")
    public String popularBooks() {
        return "books/popular";
    }
}
