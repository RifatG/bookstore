package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.services.BooksRatingAndPopulatityService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
public class PopularBooksController {

    private BooksRatingAndPopulatityService bookService;

    @Autowired
    public PopularBooksController(BooksRatingAndPopulatityService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("booksData")
    public List<Book> popularBooksAttribute() {
        return bookService.getPageOfPopularBooks(0, 5).getContent();
    }

    @GetMapping("/books/popular")
    public String popularBooks() {
        return "books/popular";
    }

    @GetMapping(value = "/books/popular", params = {"offset", "limit"})
    @ResponseBody
    public BooksPageDto popularBooks(@RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }
}
