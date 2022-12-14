package com.example.my_book_shop_app.controllers;

import com.example.my_book_shop_app.data.PaymentResponse;
import com.example.my_book_shop_app.data.RatingDto;
import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.TransactionsPageDto;
import com.example.my_book_shop_app.data.request.ChangeProfileInfoPayload;
import com.example.my_book_shop_app.data.request.PaymentPayload;
import com.example.my_book_shop_app.data.request.PaymentSuccessPayload;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BooksRatingAndPopulatityService;
import com.example.my_book_shop_app.services.PaymentService;
import com.example.my_book_shop_app.services.ProfileService;
import com.example.my_book_shop_app.services.TransactionService;
import com.example.my_book_shop_app.struct.payments.BalanceTransactionEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

@Controller
public class ProfileController {

    private final BookstoreUserRegister userRegister;
    private final ProfileService profileService;
    private final PaymentService paymentService;
    private final TransactionService transactionService;
    private final BooksRatingAndPopulatityService ratingService;
    private static final String REDIRECT_PROFILE_TOP_UP = "redirect:/profile#topup";

    @Autowired
    public ProfileController(BookstoreUserRegister userRegister, ProfileService profileService, PaymentService paymentService, TransactionService transactionService, BooksRatingAndPopulatityService ratingService) {
        this.userRegister = userRegister;
        this.profileService = profileService;
        this.paymentService = paymentService;
        this.transactionService = transactionService;
        this.ratingService = ratingService;
    }
    @ModelAttribute("currentRole")
    public String currentRole() {
        return userRegister.getCurrentUser().getRole().getName();
    }

    @ModelAttribute("transactionList")
    public List<BalanceTransactionEntity> transactionList() {
        return transactionService.getPageOfTransactions(0, 6).getContent();
    }

    @ModelAttribute("currentRating")
    public RatingDto currentRating() {
        return ratingService.getUserRating(userRegister.getCurrentUser().getId());
    }

    @GetMapping("/profile")
    public String handleProfile() {
        return "profile";
    }

    @PostMapping("/profile")
    @ResponseBody
    public ResultDto handleChangeProfileInfo(@RequestBody ChangeProfileInfoPayload payload) {
        payload.setPhone(payload.getPhone().replaceAll("\\D+", ""));
        UserEntity user = userRegister.getCurrentUser();
        profileService.changeName(user, payload.getName());
        profileService.changePassword(user, payload.getPassword());
        if(payload.isMailApproved()) {
            profileService.changeMail(user, payload.getMail());
        }
        if(payload.isPhoneApproved()) {
            profileService.changePhone(user, payload.getPhone());
        }
        return new ResultDto(true);
    }

    @PostMapping("/payment")
    @ResponseBody
    public PaymentResponse handlePayment(@RequestBody PaymentPayload payload) throws NoSuchAlgorithmException {
        String paymentUrl = paymentService.getPaymentUrl(String.format(Locale.ENGLISH, "%.2f", payload.getSum()));
        return new PaymentResponse(paymentUrl);
    }

    @PostMapping("/paymentSuccess")
    public String handlePaymentSuccess(PaymentSuccessPayload payload, RedirectAttributes redirectAttributes){
        UserEntity user = userRegister.getCurrentUser();
        if (user != null) {
            Double sum = Double.parseDouble(payload.getOutSum());
            profileService.increaseUserBalance(user, sum);
            transactionService.createPositiveTransaction(user.getId(), sum);
            redirectAttributes.addFlashAttribute("paymentSuccess", true);
        }
        return REDIRECT_PROFILE_TOP_UP;
    }

    @PostMapping("/paymentFail")
    public String handlePaymentFail(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("paymentFail", true);
        return REDIRECT_PROFILE_TOP_UP;
    }


    @GetMapping(value = "/transactions", params = {"offset", "limit"})
    @ResponseBody
    public TransactionsPageDto handleTransactionPage(@RequestParam("offset") Integer offset,
                                                     @RequestParam("limit") Integer limit) {
        return new TransactionsPageDto(transactionService.getPageOfTransactions(offset, limit).getContent());
    }
}
