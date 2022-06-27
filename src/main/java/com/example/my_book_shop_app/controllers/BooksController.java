package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.RatingDto;
import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.data.request.BookReviewRequest;
import com.example.my_book_shop_app.data.request.ChangeBookStatusRequest;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BooksRatingAndPopulatityService;
import com.example.my_book_shop_app.services.CookieHandler;
import com.example.my_book_shop_app.services.ResourceStorage;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final BooksRatingAndPopulatityService booksRatingAndPopulatityService;
    private final ResourceStorage storage;
    private final CookieHandler cookieHandler;
    private final BookstoreUserRegister userRegister;

    private static final String REDIRECT_TO_BOOKS = "redirect:/books/book/";

    @Autowired
    public BooksController(BookService bookService, ResourceStorage storage, CookieHandler cookieHandler, BooksRatingAndPopulatityService booksRatingAndPopulatityService, BookstoreUserRegister userRegister) {
        this.storage = storage;
        this.bookService = bookService;
        this.cookieHandler = cookieHandler;
        this.booksRatingAndPopulatityService = booksRatingAndPopulatityService;
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

    @GetMapping("/book/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        Book book = this.bookService.getBookBySlug(slug);
        model.addAttribute("book", book);
        RatingDto rating = new RatingDto(book.getRatingList());
        model.addAttribute("rating", rating);
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug) throws IOException {
        String savePath = storage.saveNewBookImage(file, slug);
        Book bookToUpdate = bookService.getBookBySlug(slug);
        bookToUpdate.setImage(savePath);
        bookService.updateBook(bookToUpdate);
        return REDIRECT_TO_BOOKS + slug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {

        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info(() -> "book file path: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info(() -> "book file mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info(() -> "book file data length: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(@PathVariable("slug") String slug, @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse response, Model model, @RequestBody ChangeBookStatusRequest request) {
        switch (request.getStatus()) {
            case "CART" : {
                cookieHandler.updateSlugInCookie(cartContents, "cartContents", response, slug);
                cookieHandler.removeSlugFromCookie(postponedContents, "postponedContents", response, slug);
                model.addAttribute("isCartEmpty", false);
                break;
            }
            case "KEPT" : {
                cookieHandler.updateSlugInCookie(postponedContents, "postponedContents", response, slug);
                cookieHandler.removeSlugFromCookie(cartContents, "cartContents", response, slug);
                model.addAttribute("isPostponedEmpty", false);
                break;
            }
            default: return REDIRECT_TO_BOOKS + slug;
        }
        return REDIRECT_TO_BOOKS + slug;
    }

    @PostMapping("/rateBook")
    @ResponseBody
    public ResultDto handleRateBook(@RequestBody BookReviewRequest request) {
        return new ResultDto(this.booksRatingAndPopulatityService.addRatingToBook(request.getBookId(), this.userRegister.getCurrentUser(), request.getValue()));
    }

    @PostMapping("/bookReview")
    @ResponseBody
    public ResultDto handleBookReview(@RequestBody BookReviewRequest request) {
        try {
            this.booksRatingAndPopulatityService.addBookReviewBySlug(request.getBookId(), this.userRegister.getCurrentUser(), request.getText());
        } catch (Exception e) {
            return new ResultDto(false, "Возникла ошибка " + e.getMessage());
        }
        return new ResultDto(true);
    }

    @PostMapping("/rateBookReview")
    @ResponseBody
    public ResultDto handleRateBookReview(@RequestBody BookReviewRequest request) {
        this.booksRatingAndPopulatityService.addRatingToBookReview(request.getReviewId(), this.userRegister.getCurrentUser(), request.getValue().shortValue());
        return new ResultDto(true);
    }
}
