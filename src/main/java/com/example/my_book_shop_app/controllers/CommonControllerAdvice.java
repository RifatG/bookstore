package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.CookieHandler;
import com.example.my_book_shop_app.services.UserBooksService;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {


    private static final String CART_CONTENTS_COOKIE = "cartContents";
    private static final String POSTPONED_CONTENTS_COOKIE = "postponedContents";

    private final CookieHandler cookieHandler;
    private final UserBooksService userBooksService;

    @Autowired
    @Lazy
    private BookstoreUserRegister userRegister;

    @Autowired
    public CommonControllerAdvice(CookieHandler cookieHandler, UserBooksService userBooksService) {
        this.cookieHandler = cookieHandler;
        this.userBooksService = userBooksService;
    }

    @ModelAttribute("postponedCount")
    public long postponedCount(@CookieValue(value = POSTPONED_CONTENTS_COOKIE, required = false) String postponedContents) {
        if (userRegister.isAuthenticated()) {
            return userBooksService.getKeptCount(userRegister.getCurrentUser().getId());
        } else return (postponedContents != null) ? cookieHandler.getSlugsFromCookie(postponedContents).length : 0L;
    }

    @ModelAttribute("myBooksCount")
    public long myBooksCount() {
        if (userRegister.isAuthenticated()) {
            return userBooksService.getMyBooksCount(userRegister.getCurrentUser().getId());
        } else return 0L;
    }

    @ModelAttribute("cartCount")
    public long cartCount(@CookieValue(value = CART_CONTENTS_COOKIE, required = false) String cartContents) {
        if (userRegister.isAuthenticated()) {
            return userBooksService.getCartCount(userRegister.getCurrentUser().getId());
        } else return (cartContents != null) ? cookieHandler.getSlugsFromCookie(cartContents).length : 0L;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("authenticated")
    public String isAuthenticated() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (user instanceof DefaultOAuth2User || user instanceof BookstoreUserDetails) ? "authorized" : "unauthorized";
    }

    @ModelAttribute("currentUser")
    public UserEntity currentUser() {
        return userRegister.getCurrentUser();
    }
}
