package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {

    @Lazy
    @Autowired
    private BookstoreUserRegister userRegister;

    @ModelAttribute("isAdmin")
    public boolean currentRole() {
        if (userRegister.getCurrentUser() != null)
            return userRegister.getCurrentUser().getRole().getName().equals("ADMIN");
        return false;
    }

}
