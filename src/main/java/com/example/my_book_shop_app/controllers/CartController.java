package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Controller
public class CartController {

    private final BookService bookService;
    private static final String IS_CART_EMPTY = "isCartEmpty";
    private static final String CART_CONTENTS_COOKIE = "cartContents";
    private static final String COOKIE_BOOKS_PATH = "/books";

    @Autowired
    public CartController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("reservedBooks")
    public List<Book> postponedBooksAttribute(){
        return new ArrayList<>();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/books/cart")
    public String cart(@CookieValue(value = CART_CONTENTS_COOKIE, required = false) String cartContents, Model model) {
        if (cartContents == null || cartContents.equals("")) {
            model.addAttribute(IS_CART_EMPTY, true);
        } else {
            model.addAttribute(IS_CART_EMPTY, false);
            cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
            cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length()-1) : cartContents;
            String[] cookieSlugs = cartContents.split("/");
            List<Book> booksFromCookieSlugs = this.bookService.getBooksBySlugs(cookieSlugs);
            model.addAttribute("reservedBooks", booksFromCookieSlugs);
        }
        return "cart";
    }

    @PostMapping("/books/changeBookStatus/{slug}")
    public String handleChangeBookStatus(@PathVariable("slug") String slug, @CookieValue(name = CART_CONTENTS_COOKIE, required = false) String cartContents,
                                         HttpServletResponse response, Model model) {
        if(cartContents == null || cartContents.equals("")) {
            Cookie cookie = new Cookie(CART_CONTENTS_COOKIE, slug);
            cookie.setPath(COOKIE_BOOKS_PATH);
            response.addCookie(cookie);
            model.addAttribute(IS_CART_EMPTY, false);
        } else if(!cartContents.contains(slug)){
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cartContents).add(slug);
            Cookie cookie = new Cookie(CART_CONTENTS_COOKIE, stringJoiner.toString());
            cookie.setPath(COOKIE_BOOKS_PATH);
            response.addCookie(cookie);
            model.addAttribute(IS_CART_EMPTY, false);
        }
        return "redirect:/books/book/" + slug;
    }

    @PostMapping("/books/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = CART_CONTENTS_COOKIE, required = false) String cartContents,
                                                  HttpServletResponse response, Model model) {
        if(cartContents != null && !cartContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie(CART_CONTENTS_COOKIE, String.join("/", cookieBooks));
            cookie.setPath(COOKIE_BOOKS_PATH);
            response.addCookie(cookie);
            model.addAttribute(IS_CART_EMPTY, false);
        } else {
            model.addAttribute(IS_CART_EMPTY, true);
        }
        return "redirect:/books/cart";
    }

}
