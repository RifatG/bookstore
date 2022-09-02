package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.UserBooksService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MyBooksPageController {

    private final BookstoreUserRegister userRegister;
    private final UserBooksService userBooksService;

    @Autowired
    public MyBooksPageController(BookstoreUserRegister userRegister, UserBooksService userBooksService) {
        this.userRegister = userRegister;
        this.userBooksService = userBooksService;
    }
    @ModelAttribute("booksData")
    public List<Book> popularBooksAttribute() {
        return userBooksService.getBooksOfUser(userRegister.getCurrentUser().getId());
    }

    @GetMapping("/my")
    public String handleMy() {
        return "my";
    }
}
