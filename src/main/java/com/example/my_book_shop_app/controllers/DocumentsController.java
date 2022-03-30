package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.SearchWordDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class DocumentsController {

    @GetMapping("/documents")
    public String documents() {
        return "documents/index";
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }
}
