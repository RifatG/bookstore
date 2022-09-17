package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.request.FeedbackPayload;
import com.example.my_book_shop_app.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ContactsController {

    private final MailService mailService;

    @Autowired
    public ContactsController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    @PostMapping("/contacts")
    @ResponseBody
    public ResultDto handleContacts(@RequestBody FeedbackPayload payload) {
        String subject = "Issue: " + payload.getSubject();
        String text = String.format("The new issue from %s with email %s: %n%s", payload.getName(), payload.getMail(), payload.getText());
        this.mailService.sendMailToUs(subject, text);
        return new ResultDto(true);
    }
}
