package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.PaymentResponse;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.data.request.ChangeProfileInfoPayload;
import com.example.my_book_shop_app.data.request.PaymentPayload;
import com.example.my_book_shop_app.data.request.PaymentSuccessPayload;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.PaymentService;
import com.example.my_book_shop_app.services.ProfileService;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@Controller
public class ProfileController {

    private final BookstoreUserRegister userRegister;
    private final ProfileService profileService;
    private final PaymentService paymentService;
    private static final String REDIRECT_PROFILE_TOP_UP = "redirect:/profile#topup";

    @Autowired
    public ProfileController(BookstoreUserRegister userRegister, ProfileService profileService, PaymentService paymentService) {
        this.userRegister = userRegister;
        this.profileService = profileService;
        this.paymentService = paymentService;
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

    @GetMapping("/profile")
    public String handleProfile() {
        return "profile";
    }

    @PostMapping("/profile")
    public String handleChangeProfileInfo(ChangeProfileInfoPayload payload) {
        profileService.changePassword(currentUser(), payload.getPassword());
        return "redirect:/profile";
    }

    @PostMapping("/payment")
    @ResponseBody
    public PaymentResponse handlePayment(@RequestBody PaymentPayload payload) throws NoSuchAlgorithmException {
        String paymentUrl = paymentService.getPaymentUrl(String.format(Locale.ENGLISH, "%.2f", payload.getSum()));
        return new PaymentResponse(paymentUrl);
    }

    @PostMapping("/paymentSuccess")
    public String handlePaymentSuccess(PaymentSuccessPayload payload, RedirectAttributes redirectAttributes){
        profileService.topUpUserBalance(currentUser(), Double.parseDouble(payload.getOutSum()));
        redirectAttributes.addFlashAttribute("paymentSuccess", true);
        return REDIRECT_PROFILE_TOP_UP;
    }

    @PostMapping("/paymentFail")
    public String handlePaymentFail(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("paymentFail", true);
        return REDIRECT_PROFILE_TOP_UP;
    }
}
