package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.BooksPageDto;
import com.example.my_book_shop_app.services.TagService;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TagsController {

    private final TagService tagService;

    public TagsController(TagService tagService) {
        this.tagService = tagService;
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
