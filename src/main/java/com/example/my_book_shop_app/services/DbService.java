package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.Book2UserRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.links.Book2UserEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DbService {

    private final BookService bookService;
    private final Book2UserRepository book2UserRepository;
    private final CookieHandler cookieHandler;

    private static final int KEPT_STATUS_ID = 1;
    private static final int CART_STATUS_ID = 2;
    private static final String CART_CONTENTS_COOKIE = "cartContents";
    private static final String POSTPONED_CONTENTS_COOKIE = "postponedContents";

    @Autowired
    public DbService(BookService bookService, Book2UserRepository book2UserRepository, CookieHandler cookieHandler) {
        this.bookService = bookService;
        this.book2UserRepository = book2UserRepository;
        this.cookieHandler = cookieHandler;
    }

    public void moveBooksFromCookieToDb(UserEntity user, Cookie[] cookies, HttpServletResponse response){
        Integer userId = user.getId();
        List<Book> booksInCart = null;
        List<Book> booksInKept = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                switch (cookie.getName()) {
                    case CART_CONTENTS_COOKIE: {
                        booksInCart = bookService.getBooksBySlugs(cookieHandler.getSlugsFromCookie(cookie.getValue()));
                        cookieHandler.removeAllSlugs(CART_CONTENTS_COOKIE, response);
                        break;
                    } case POSTPONED_CONTENTS_COOKIE:{
                        booksInKept = bookService.getBooksBySlugs(cookieHandler.getSlugsFromCookie(cookie.getValue()));
                        cookieHandler.removeAllSlugs(POSTPONED_CONTENTS_COOKIE, response);
                        break;
                    } default: break;
                }
            }
            if (booksInCart != null) booksInCart.forEach(book -> setBooksAs(userId, book.getId(), CART_STATUS_ID));
            if (booksInKept != null) booksInKept.forEach(book -> setBooksAs(userId, book.getId(), KEPT_STATUS_ID));
        }
    }

    private void setBooksAs(Integer userId, Integer bookId, int statusId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            Book2UserEntity book2User = new Book2UserEntity();
            book2User.setBookId(bookId);
            book2User.setUserId(userId);
            book2User.setTime(LocalDateTime.now());
            book2User.setTypeId(statusId);
            this.book2UserRepository.save(book2User);
        }
    }


}
