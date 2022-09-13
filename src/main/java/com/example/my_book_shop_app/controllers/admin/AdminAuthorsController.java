package com.example.my_book_shop_app.controllers.admin;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminDeleteElementPayload;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminElementChangePayload;
import com.example.my_book_shop_app.services.AuthorService;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.ResourceStorage;
import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public ResultDto createBookForAuthor(@PathVariable("slug") String slug,
                                         @RequestParam("image") MultipartFile image,
                                         @RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestParam("price") String priceString,
                                         @RequestParam("discount") String discountString) throws IOException {
        Author author = authorService.getAuthorBySlug(slug);
        return bookService.createNewBookWithValidation(title, description, priceString, discountString, image, author);
    }

    @PostMapping("/authors/delete")
    @ResponseBody
    public ResultDto deleteAuthor(@RequestBody AdminDeleteElementPayload payload){
        Author author = authorService.getAuthorById(payload.getElementId());
        bookService.deleteBooks(author.getBookList());
        authorService.deleteAuthor(author);
        return new ResultDto(true);
    }
}
