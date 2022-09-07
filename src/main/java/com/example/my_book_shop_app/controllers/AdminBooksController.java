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
        String title = payload.getValue();
        if (title == null || title.equals("")) {
            return new ResultDto(false, "Title can't be empty");
        }
        if(!bookService.isThereBookWithTitle(title)) {
            bookToUpdate.setTitle(title);
            bookService.updateBook(bookToUpdate);
            return new ResultDto(true);
        }
        return new ResultDto(false, "There is already a book with such title");
    }

    @PostMapping("/{slug}/description")
    @ResponseBody
    public ResultDto saveNewBookDescription(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload){
        Book bookToUpdate = bookService.getBookBySlug(slug);
        String description = payload.getValue();
        if (description == null || description.equals("")) {
            return new ResultDto(false, "Description can't be empty");
        }
        bookToUpdate.setDescription(payload.getValue());
        bookService.updateBook(bookToUpdate);
        return new ResultDto(true);
    }

    @PostMapping("/{slug}/old_price")
    @ResponseBody
    public ResultDto saveNewBookOldPrice(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload){
        Book bookToUpdate = bookService.getBookBySlug(slug);
        String priceString = payload.getValue();
        int price;
        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            return new ResultDto(false, "Old price must be digit");
        }
        bookToUpdate.setPrice(price);
        bookService.updateBook(bookToUpdate);
        return new ResultDto(true);
    }

    @PostMapping("/{slug}/discount")
    @ResponseBody
    public ResultDto saveNewBookDiscount(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload){
        Book bookToUpdate = bookService.getBookBySlug(slug);
        String discountString = payload.getValue();
        int discount;
        try {
            discount = Integer.parseInt(discountString);
            if (discount < 0 || discount > 100) return new ResultDto(false, "Discount must be more than 0 and less than 100");
        } catch (NumberFormatException e) {
            return new ResultDto(false, "Discount must be digit");
        }
        bookToUpdate.setDiscount((byte) discount);
        bookService.updateBook(bookToUpdate);
        return new ResultDto(true);
    }
}
