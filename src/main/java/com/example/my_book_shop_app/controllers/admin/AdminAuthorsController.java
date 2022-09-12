package com.example.my_book_shop_app.controllers.admin;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminElementChangePayload;
import com.example.my_book_shop_app.services.AuthorService;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.ResourceStorage;
import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class AdminAuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final ResourceStorage storage;

    @Autowired
    public AdminAuthorsController(AuthorService authorService, BookService bookService, ResourceStorage storage) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.storage = storage;
    }

    @PostMapping("/authors/{slug}/name")
    public ResultDto saveNewAuthorName(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload) {
        String authorName = payload.getValue();
        if (authorName == null || authorName.equals("") || authorName.length() > 100) return new ResultDto(false, "Author name must contain from 1 till 100 symbols");
        if(!authorService.isThereAuthorWithName(authorName)) {
            Author author = authorService.getAuthorBySlug(slug);
            author.setName(authorName);
            authorService.updateAuthor(author);
            return new ResultDto(true);
        }
        return new ResultDto(false, "There is already an author with such name");
    }

    @PostMapping("/authors")
    public ResultDto createAuthor(@RequestParam("photo") MultipartFile photo, @RequestParam("name") String authorName, @RequestParam("description") String description) {
        if (authorName == null || authorName.equals("") || authorName.length() > 100) return new ResultDto(false, "Author name must contain from 1 till 100 symbols");
        if (authorService.isThereAuthorWithName(authorName)) return new ResultDto(false, "There is already an author with such name");
        if (description == null || description.equals("")) return new ResultDto(false, "Author description must be not empty");
        try {
            String slug = UUID.randomUUID().toString().substring(0, 10);
            String savePath = storage.saveNewAuthorImage(photo, slug);
            authorService.createNewAuthor(authorName, description, slug, savePath);
        } catch (Exception e) {
            return new ResultDto(false, e.getMessage());
        }
        return new ResultDto(true);
    }

    @PostMapping("/authors/{slug}/description")
    public ResultDto saveNewAuthorDescription(@PathVariable("slug") String slug, @RequestBody AdminElementChangePayload payload){
        Author author = authorService.getAuthorBySlug(slug);
        String description = payload.getValue();
        if (description == null || description.equals("")) {
            return new ResultDto(false, "Description can't be empty");
        }
        author.setDescription(payload.getValue());
        authorService.updateAuthor(author);
        return new ResultDto(true);
    }

    @PostMapping("/authors/{slug}/books")
    public ResultDto createBookForAuthor(@PathVariable("slug") String slug,
                                         @RequestParam("image") MultipartFile image,
                                         @RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestParam("price") String priceString,
                                         @RequestParam("discount") String discountString) throws IOException {
        Author author = authorService.getAuthorBySlug(slug);
        if (title == null || title.equals("")) {
            return new ResultDto(false, "Description can't be empty");
        }
        if (description == null || description.equals("")) {
            return new ResultDto(false, "Description can't be empty");
        }
        int price;
        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            return new ResultDto(false, "Old price must be digit");
        }
        int discount;
        try {
            discount = Integer.parseInt(discountString);
            if (discount < 0 || discount > 100) return new ResultDto(false, "Discount must be more than 0 and less than 100");
        } catch (NumberFormatException e) {
            return new ResultDto(false, "Discount must be digit");
        }
        String bookSlug = UUID.randomUUID().toString().substring(0, 10);
        String savePath = storage.saveNewBookImage(image, bookSlug);
        bookService.createNewBook(title, slug, description, savePath, price, discount, author);
        return new ResultDto(true);
    }
}
