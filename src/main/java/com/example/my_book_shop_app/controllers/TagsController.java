package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.TagService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TagsController {

    private final TagService tagService;
    private final BookstoreUserRegister userRegister;

    public TagsController(TagService tagService, BookstoreUserRegister userRegister) {
        this.tagService = tagService;
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

    @GetMapping("/tags")
    public String bookList(@RequestParam("tagId") Integer tagId, Model model){
        model.addAttribute("booksData", tagService.getPageOfBooksByTagIdBySql(tagId, 0, 5).getContent());
        model.addAttribute("tag", tagService.getTagById(tagId));
        return "tags/index";
    }

    @GetMapping("/books/tag/{tagId}")
    @ResponseBody
    public BooksPageDto bookList(@PathVariable(value = "tagId") Integer tagId,
                                 @RequestParam("offset") Integer offset,
                                 @RequestParam("limit") Integer limit) {
        Page<Book> page = this.tagService.getPageOfBooksByTagIdBySql(tagId, offset, limit);
        return new BooksPageDto(page.getContent());
    }
}
