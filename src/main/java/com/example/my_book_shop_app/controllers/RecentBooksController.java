package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class RecentBooksController {

    private final BookService bookService;

    @Autowired
    public RecentBooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("booksData")
    public List<Book> recentBooksAttribute() {
        return bookService.getPageOfRecentBooks( 0, 5).getContent();
    }

    @GetMapping("/books/recent")
    public String recentBooks() {
        return "books/recent";
    }

    @GetMapping(value = "/books/recent", params = {"from", "to", "offset", "limit"})
    @ResponseBody
    public BooksPageDto recentBooks(@RequestParam("from") String from,
                              @RequestParam("to") String to, @RequestParam("offset") Integer offset,
                              @RequestParam("limit") Integer limit) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return new BooksPageDto(bookService.getPageOfBooksByDateRange(dateFormat.parse(from), dateFormat.parse(to), offset, limit).getContent());
    }
}
