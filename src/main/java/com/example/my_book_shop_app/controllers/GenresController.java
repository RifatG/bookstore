package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.GenreService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.genre.GenreEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GenresController {

    private final GenreService genreService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public GenresController(GenreService genreService, BookstoreUserRegister userRegister) {
        this.genreService = genreService;
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

    @ModelAttribute("genreList")
    public List<GenreEntity> genreList() {
        return genreService.getGenreList();
    }

    @GetMapping("/genres")
    public String genres() {
        return "genres/index";
    }

    @GetMapping("/genres/slug")
    public String bookList(@RequestParam("genreId") Integer genreId, Model model){
        model.addAttribute("booksData", genreService.getPageOfBooksByGenreIdBySql(genreId, 0, 5).getContent());
        model.addAttribute("genre", genreService.getGenreById(genreId));
        return "genres/slug";
    }

    @GetMapping("/books/genre/{genreId}")
    @ResponseBody
    public BooksPageDto bookList(@PathVariable(value = "genreId") Integer genreId,
                                 @RequestParam("offset") Integer offset,
                                 @RequestParam("limit") Integer limit) {
        Page<Book> page = this.genreService.getPageOfBooksByGenreIdBySql(genreId, offset, limit);
        return new BooksPageDto(page.getContent());
    }
}
