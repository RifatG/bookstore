package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.ContactConfirmationPayload;
import com.example.my_book_shop_app.data.ContactConfirmationResponse;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.exceptions.ConfirmationCodeException;
import com.example.my_book_shop_app.exceptions.UserAlreadyExistException;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.security.RegistrationForm;
import com.example.my_book_shop_app.services.ConfirmationCodeService;
import com.example.my_book_shop_app.struct.external_api.ConfirmationCode;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthenticationController {

    private final BookstoreUserRegister userRegister;
    private final ConfirmationCodeService confirmationCodeService;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    public AuthenticationController(BookstoreUserRegister userRegister, ConfirmationCodeService confirmationCodeService) {
        this.userRegister = userRegister;
        this.confirmationCodeService = confirmationCodeService;
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

    @GetMapping("/signin")
    public String signIn() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        return new ContactConfirmationResponse(true);
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) {
        try {
            String confirmationCodeString = confirmationCodeService.sendEmailConfirmationCode(payload.getContact());
            confirmationCodeService.saveNewConfirmationCode(new ConfirmationCode(confirmationCodeString, 300));
        } catch (ConfirmationCodeException e) {
            logger.error(e.getMessage());
            return new ContactConfirmationResponse(e.getMessage());
        }
        return new ContactConfirmationResponse(true);
    }

    @PostMapping("/requestPhoneConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestPhoneConfirmation(@RequestBody ContactConfirmationPayload payload) {
        try {
            String confirmationCodeString = confirmationCodeService.sendPhoneConfirmationCode(payload.getContact());
            confirmationCodeService.saveNewConfirmationCode(new ConfirmationCode(confirmationCodeString, 60));
        } catch (ConfirmationCodeException e) {
            logger.error("Confirmation code wasn't sent. Error: {}", e.getMessage());
            return new ContactConfirmationResponse(e.getMessage());
        }
        return new ContactConfirmationResponse(true);
    }

    @PostMapping("/registration")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) throws UserAlreadyExistException {
        registrationForm.setPhoneNumber(registrationForm.getPhoneNumber().replaceAll("\\D+", ""));
        userRegister.registerNewUser(registrationForm);
        model.addAttribute("registrationOk", true);
        return "signin";
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        if (Boolean.TRUE.equals(confirmationCodeService.verifyConfirmationCode(payload.getCode()))) {
            response.setResult(true);
        } else {
            response.setResult(false);
            response.setError("Incorrect code. Please try again");
        }
        return response;
    }

    @PostMapping("/login-by-email")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload, HttpServletResponse httpServletResponse) {
        try{
            ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
            Cookie cookie = new Cookie("token", loginResponse.getToken());
            httpServletResponse.addCookie(cookie);
            return loginResponse;
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error("User not found with email {}", payload.getContact());
            return new ContactConfirmationResponse(usernameNotFoundException.getMessage());
        } catch (Exception e) {
            logger.error("LOGIN error: {}", e.getMessage());
            return null;
        }
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload, HttpServletResponse httpServletResponse) {
        payload.setContact(payload.getContact().replaceAll("\\D+", ""));
            if (Boolean.TRUE.equals(confirmationCodeService.verifyConfirmationCode(payload.getCode()))) {
            try{
                ContactConfirmationResponse loginResponse = userRegister.jwtLoginByPhoneNumber(payload);
                Cookie cookie = new Cookie("token", loginResponse.getToken());
                httpServletResponse.addCookie(cookie);
                return loginResponse;
            } catch (UsernameNotFoundException usernameNotFoundException) {
                logger.error("User not found with phone {}", payload.getContact());
                return new ContactConfirmationResponse(usernameNotFoundException.getMessage());
            } catch (Exception e) {
                logger.error("LOGIN error: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    @GetMapping("/my")
    public String handleMy(Model model) {
        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        return "profile";
    }
}
