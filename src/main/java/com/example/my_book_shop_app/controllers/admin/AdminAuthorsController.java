package com.example.my_book_shop_app.controllers.admin;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminElementChangePayload;
import com.example.my_book_shop_app.services.AuthorService;
import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminAuthorsController {

    private final AuthorService authorService;

    @Autowired
    public AdminAuthorsController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/authors/{authorId}/name")
    public ResultDto saveNewAuthorName(@PathVariable("authorId") int authorId, @RequestBody AdminElementChangePayload payload) {
        String authorName = payload.getValue();
        if (authorName == null || authorName.equals("") || authorName.length() > 100) return new ResultDto(false, "Author name must contain from 1 till 100 symbols");
        if(!authorService.isThereAuthorWithName(authorName)) {
            Author author = authorService.getAuthorById(authorId);
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
            authorService.createNewAuthor(authorName, description, photo);
        } catch (Exception e) {
            return new ResultDto(false, e.getMessage());
        }
        return new ResultDto(true);
    }
}
