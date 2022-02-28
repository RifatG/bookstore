package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.Book;
import com.example.my_book_shop_app.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class CartController {

    private BookService bookService;

    @Autowired
    public CartController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("reservedBooks")
    public List<Book> postponedBooksAttribute(){
        return bookService.getReservedBooksData();
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }
}
