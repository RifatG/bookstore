package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BooksRatingAndPopulatityService;
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
import java.util.List;

@Controller
public class PopularBooksController {

    private final BooksRatingAndPopulatityService bookPopularityService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public PopularBooksController(BooksRatingAndPopulatityService bookService, BookstoreUserRegister userRegister) {
        this.bookPopularityService = bookService;
        this.userRegister = userRegister;
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

    @ModelAttribute("booksData")
    public List<Book> popularBooksAttribute() {
        return bookPopularityService.getPageOfPopularBooksBySql(0, 5);
    }

    @ModelAttribute("booksCount")
    public Integer popularBooksCount() {
        return bookPopularityService.getCountOfPopularBooks();
    }

    @GetMapping("/books/popular")
    public String popularBooks() {
        return "books/popular";
    }

    @GetMapping(value = "/books/popular", params = {"offset", "limit"})
    @ResponseBody
    public BooksPageDto popularBooks(@RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookPopularityService.getPageOfPopularBooksBySql(offset, limit));
    }
}
