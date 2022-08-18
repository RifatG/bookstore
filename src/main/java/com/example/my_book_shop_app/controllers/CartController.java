package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.CookieHandler;
import com.example.my_book_shop_app.services.PaymentService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private final BookService bookService;
    private final CookieHandler cookieHandler;
    private final BookstoreUserRegister userRegister;
    private final PaymentService paymentService;

    private static final String IS_CART_EMPTY = "isCartEmpty";
    private static final String CART_CONTENTS_COOKIE = "cartContents";

    @Autowired
    public CartController(BookService bookService, CookieHandler cookieHandler, BookstoreUserRegister userRegister, PaymentService paymentService) {
        this.bookService = bookService;
        this.cookieHandler = cookieHandler;
        this.userRegister = userRegister;
        this.paymentService = paymentService;
    }

    @ModelAttribute("cartBooks")
    public List<Book> cartBooksAttribute(){
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

    @GetMapping("/books/cart")
    public String cart(@CookieValue(value = CART_CONTENTS_COOKIE, required = false) String cartContents, Model model) {
        if (cartContents == null || cartContents.equals("")) {
            model.addAttribute(IS_CART_EMPTY, true);
        } else {
            model.addAttribute(IS_CART_EMPTY, false);
            List<Book> booksFromCookieSlugs = this.bookService.getBooksBySlugs(this.cookieHandler.getSlugsFromCookie(cartContents));
            Integer totalPrice = booksFromCookieSlugs.stream().mapToInt(Book::getPrice).sum();
            Integer totalDiscountPrice = booksFromCookieSlugs.stream().mapToInt(Book::getDiscountPrice).sum();
            model.addAttribute("cartBooks", booksFromCookieSlugs);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("totalDiscountPrice", totalDiscountPrice);

        }
        return "cart";
    }

    @PostMapping("/books/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = CART_CONTENTS_COOKIE, required = false) String cartContents,
                                                  HttpServletResponse response, Model model) {
        this.cookieHandler.removeSlugFromCookie(cartContents, CART_CONTENTS_COOKIE, response, slug);
        model.addAttribute(IS_CART_EMPTY, cartContents == null || cartContents.equals(""));
        return "redirect:/books/cart";
    }

    @GetMapping("/books/pay")
    public RedirectView handlePay(@CookieValue(name = CART_CONTENTS_COOKIE, required = false) String cartContents) throws NoSuchAlgorithmException {
        String paymentUrl = paymentService.getPaymentUrl("100");
        return new RedirectView(paymentUrl);
    }
}
