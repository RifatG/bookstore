package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewedBooksController {

    private final BookstoreUserRegister userRegister;
    private final BookService bookService;

    @Autowired
    public ViewedBooksController(BookstoreUserRegister userRegister, BookService bookService) {
        this.userRegister = userRegister;
        this.bookService = bookService;
    }

    @ModelAttribute("booksData")
    public List<Book> recentBooksAttribute() {
        if (userRegister.isAuthenticated())
            return bookService.getPageOfViewedBooks(currentUser().getId() ,0, 5).getContent();
        else return new ArrayList<>();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("currentUser")
    public UserEntity currentUser() {
        return userRegister.getCurrentUser();
    }

    @ModelAttribute("authenticated")
    public String isAuthenticated() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (user instanceof DefaultOAuth2User || user instanceof BookstoreUserDetails) ? "authorized" : "unauthorized";
    }

    @GetMapping("/books/viewed")
    public String viewedBooks() {
        return "books/viewed";
    }

    @GetMapping(value = "/books/viewed", params = {"offset", "limit"})
    @ResponseBody
    public BooksPageDto viewedBooks(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfViewedBooks(currentUser().getId() , offset, limit).getContent());
    }
}
