package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.request.AdminElementChangePayload;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class AdminBooksController {

    private final BookService bookService;

    @Autowired
    public AdminBooksController(BookService bookService) {
        this.bookService = bookService;
    }


    @PostMapping("/{slug}/title")
    @ResponseBody
    public ResultDto saveNewBookTitle(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload){
        Book bookToUpdate = bookService.getBookBySlug(slug);
        String newTitle = payload.getValue();
        if(!bookService.isThereBookWithTitle(newTitle)) {
            bookToUpdate.setTitle(newTitle);
            bookService.updateBook(bookToUpdate);
            return new ResultDto(true);
        }
        return new ResultDto(false, "There is already a book with such title");
    }

    @PostMapping("/{slug}/description")
    @ResponseBody
    public ResultDto saveNewBookDescription(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload){
        Book bookToUpdate = bookService.getBookBySlug(slug);
        bookToUpdate.setDescription(payload.getValue());
        bookService.updateBook(bookToUpdate);
        return new ResultDto(true);
    }
}
