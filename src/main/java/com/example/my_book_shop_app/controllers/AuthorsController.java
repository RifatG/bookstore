package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.AuthorService;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.struct.author.Author;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Api("authors data")
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public AuthorsController(AuthorService authorService, BookService bookService, BookstoreUserRegister userRegister) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.userRegister = userRegister;
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<Author>> authorsMap(){
        return authorService.getAuthorsMap();
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
    @GetMapping("/authors")
    public String authorsPage(){
        return "/authors/index";
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public Map<String, List<Author>> authors() {
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors/slug")
    public String bookList(@RequestParam("authorId") Integer authorId, Model model){
        model.addAttribute("booksData", bookService.getPageOfBooksByAuthorId(authorId, 0, 5).getContent());
        model.addAttribute("author", authorService.getAuthorById(authorId));
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
}
