package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.SearchWordDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class SignInController {

    @GetMapping("/signin")
    public String signIn() {
        return "signin";
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }
}
