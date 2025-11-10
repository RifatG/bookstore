package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.services.AuthorService;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.ResourceStorage;
import com.example.my_book_shop_app.struct.author.Author;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final ResourceStorage storage;

    @Autowired
    public AuthorsController(AuthorService authorService, BookService bookService, ResourceStorage storage) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.storage = storage;
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<Author>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage(){
        return "/authors/index";
    }

    @GetMapping("/api/authors")
    @ResponseBody
    public Map<String, List<Author>> authors() {
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors/{slug}")
    public String bookList(@PathVariable("slug") String slug, Model model){
        Author author = authorService.getAuthorBySlug(slug);
        model.addAttribute("booksData", bookService.getPageOfBooksByAuthorId(author.getId(), 0, 5).getContent());
        model.addAttribute("author", author);
        return "authors/slug";
    }

    @GetMapping("/books/author/{authorId}")
    @ResponseBody
    public BooksPageDto bookList(@PathVariable(value = "authorId") Integer authorId,
                                 @RequestParam("offset") Integer offset,
                                 @RequestParam("limit") Integer limit) {
        Page<Book> page = this.bookService.getPageOfBooksByAuthorId(authorId, offset, limit);
        return new BooksPageDto(page.getContent());
    }

    @GetMapping("/books/author")
    public String authorsBooks(@RequestParam("authorId") Integer authorId, Model model){
        model.addAttribute("booksData", bookService.getPageOfBooksByAuthorId(authorId, 0, 5).getContent());
        model.addAttribute("author", authorService.getAuthorById(authorId));
        return "/books/author";
    }

    @PostMapping("/authors/{slug}/img/save")
    public String saveNewAuthorImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug) throws IOException {
        String savePath = storage.saveNewBookImage(file, slug);
        Author author = authorService.getAuthorBySlug(slug);
        author.setPhoto(savePath);
        authorService.updateAuthor(author);
        return "redirect:/authors/" + slug;
    }
}
