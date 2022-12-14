package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.BooksRatingAndPopulatityService;
import com.example.my_book_shop_app.services.TagService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.tags.TagsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final BooksRatingAndPopulatityService bookRatingService;
    private final TagService tagService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public MainPageController(BookService bookService, BooksRatingAndPopulatityService bookRatingService, TagService tagService, BookstoreUserRegister userRegister) {
        this.bookService = bookService;
        this.bookRatingService = bookRatingService;
        this.tagService = tagService;
        this.userRegister = userRegister;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks(){
        return userRegister.isAuthenticated() ? bookRatingService.getPageOfRecommendedBooks(userRegister.getCurrentUser().getId(), 0, 6) : bookRatingService.getPageOfHighRatingBooks(0, 6);
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks() {
        return bookRatingService.getPageOfPopularBooksBySql(0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<Book> recentBooks() {
        return bookService.getPageOfRecentBooks(0, 6).getContent();
    }

    @ModelAttribute("tagList")
    public List<TagsEntity> tagList() {
        return tagService.getTagList();
    }


    @GetMapping("/")
    public String mainPage(){
        return "index";
    }
}
