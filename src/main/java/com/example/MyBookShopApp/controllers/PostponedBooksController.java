package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BookService;
import com.sun.tracing.dtrace.Attributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class PostponedBooksController {

    private final BookService bookService;

    @Autowired
    public PostponedBooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("postponedBooks")
    public List<Book> postponedBooksAttribute(){
        return bookService.getPostponedBooksData();
    }

    @GetMapping("/postponed")
    public String postponedBooks() {
        return "postponed";
    }
}
