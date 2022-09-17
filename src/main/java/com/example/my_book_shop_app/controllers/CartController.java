package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.exceptions.LowUserBalanceException;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.*;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CartController {

    private final BookService bookService;
    private final CookieHandler cookieHandler;
    private final BookstoreUserRegister userRegister;
    private final ProfileService profileService;
    private final TransactionService transactionService;
    private final UserBooksService userBooksService;

    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    private static final String IS_CART_EMPTY = "isCartEmpty";
    private static final String CART_CONTENTS_COOKIE = "cartContents";

    @Autowired
    public CartController(BookService bookService, CookieHandler cookieHandler, BookstoreUserRegister userRegister, ProfileService profileService, TransactionService transactionService, UserBooksService userBooksService) {
        this.bookService = bookService;
        this.cookieHandler = cookieHandler;
        this.userRegister = userRegister;
        this.profileService = profileService;
        this.transactionService = transactionService;
        this.userBooksService = userBooksService;
    }

    @ModelAttribute("cartBooks")
    public List<Book> cartBooksAttribute(){
        return new ArrayList<>();
    }

    @GetMapping("/books/cart")
    public String cart(@CookieValue(value = CART_CONTENTS_COOKIE, required = false) String cartContents, Model model) {
        List<Book> cartBooks;
        cartBooks = userRegister.isAuthenticated()
                ? userBooksService.getBooksInCartOfUser(userRegister.getCurrentUser().getId())
                : this.bookService.getBooksBySlugs(this.cookieHandler.getSlugsFromCookie(cartContents));
        if (cartBooks != null && !cartBooks.isEmpty()) {
            model.addAttribute(IS_CART_EMPTY, false);
            Integer totalPrice = cartBooks.stream().mapToInt(Book::getPrice).sum();
            Integer totalDiscountPrice = cartBooks.stream().mapToInt(Book::getDiscountPrice).sum();
            model.addAttribute("cartBooks", cartBooks);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("totalDiscountPrice", totalDiscountPrice);
        } else {
            model.addAttribute(IS_CART_EMPTY, true);
        }
        return "cart";
    }

    @PostMapping("/books/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = CART_CONTENTS_COOKIE, required = false) String cartContents,
                                                  HttpServletResponse response, Model model) {
        if (userRegister.isAuthenticated()) {
            UserEntity user = userRegister.getCurrentUser();
            this.userBooksService.removeBookFromCart(user.getId(), bookService.getBookBySlug(slug).getId());
            model.addAttribute(IS_CART_EMPTY, userBooksService.getBooksInCartOfUser(user.getId()).isEmpty());
        } else {
            this.cookieHandler.removeSlugFromCookie(cartContents, CART_CONTENTS_COOKIE, response, slug);
            model.addAttribute(IS_CART_EMPTY, cartContents == null || cartContents.equals(""));
        }
        return "redirect:/books/cart";
    }

    @GetMapping("/books/buy")
    public String handlePay(RedirectAttributes redirectAttributes, HttpServletResponse response) {
        if (!userRegister.isAuthenticated()) {
            logger.info("User is not authenticated. It is need to be authenticated to buy a book");
            return "signin";
        }
        UserEntity user = userRegister.getCurrentUser();
        List<Book> cartBooks = this.userBooksService.getBooksInCartOfUser(user.getId());

        try {
            final List<Book> boughtBooks = new ArrayList<>();
            cartBooks.forEach(book -> {
                if (userBooksService.setBookAsPaid(user.getId(), book.getId()) != null) {
                    boughtBooks.add(book);
                    logger.info("Status of book {} of user {} has been changed to PAID", book.getTitle(), user.getName());
                } else {
                    logger.info("Status of book {} of user {} has NOT been changed to PAID", book.getTitle(), user.getName());
                }
            });
            Double totalPrice = boughtBooks.stream().mapToDouble(Book::getDiscountPrice).sum();
            this.profileService.reduceUserBalance(user, totalPrice);
            boughtBooks.forEach(book -> this.transactionService.createNegativeTransaction(
                        user.getId(), book, book.getDiscountPrice()*-1d));
            redirectAttributes.addFlashAttribute("BoughtBooks", boughtBooks.stream().map(Book::getTitle).collect(Collectors.toList()));
            this.cookieHandler.removeAllSlugs(CART_CONTENTS_COOKIE, response);
        } catch (LowUserBalanceException e) {
            logger.info(e.getMessage());
            redirectAttributes.addFlashAttribute("BalanceNotEnough", true);
        }
        return "redirect:/books/cart";

    }
}
