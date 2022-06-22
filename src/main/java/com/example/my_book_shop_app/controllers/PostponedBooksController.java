package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.CookieHandler;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PostponedBooksController {

    private final BookService bookService;
    private final CookieHandler cookieHandler;
    private final BookstoreUserRegister userRegister;

    private static final String IS_POSTPONED_EMPTY = "isPostponedEmpty";
    private static final String POSTPONED_CONTENTS_COOKIE = "postponedContents";

    @Autowired
    public PostponedBooksController(BookService bookService, CookieHandler cookieHandler, BookstoreUserRegister userRegister) {
        this.bookService = bookService;
        this.cookieHandler = cookieHandler;
        this.userRegister = userRegister;
    }

    @ModelAttribute("postponedBooks")
    public List<Book> postponedBooksAttribute(){
        return new ArrayList<>();
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

    @GetMapping("/books/postponed")
    public String cart(@CookieValue(value = POSTPONED_CONTENTS_COOKIE, required = false) String postponedContents, Model model) {
        if (postponedContents == null || postponedContents.equals("")) {
            model.addAttribute(IS_POSTPONED_EMPTY, true);
        } else {
            model.addAttribute(IS_POSTPONED_EMPTY, false);
            List<Book> booksFromCookieSlugs = this.bookService.getBooksBySlugs(this.cookieHandler.getSlugsFromCookie(postponedContents));
            model.addAttribute("postponedBooks", booksFromCookieSlugs);
        }
        return "postponed";
    }

    @PostMapping("/books/changeBookStatus/postponed/remove/{slug}")
    public String handleRemoveBookFromPostponedRequest(@PathVariable("slug") String slug, @CookieValue(name = POSTPONED_CONTENTS_COOKIE, required = false) String postponedContents,
                                                  HttpServletResponse response, Model model) {
        this.cookieHandler.removeSlugFromCookie(postponedContents, POSTPONED_CONTENTS_COOKIE, response, slug);
        model.addAttribute(IS_POSTPONED_EMPTY, postponedContents == null || postponedContents.equals(""));
        return "redirect:/books/postponed";
    }
}
