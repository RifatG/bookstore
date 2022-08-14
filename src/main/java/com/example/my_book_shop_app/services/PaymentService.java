package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;


    public String getPaymentUrl(List<Book> booksFromCookieSlugs) throws NoSuchAlgorithmException {
        double paymentSumTotal = booksFromCookieSlugs.stream().mapToDouble(Book::getDiscountPrice).sum();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = "5"; //just for testing TODO order indexing later
        md.update((merchantLogin + ":" + paymentSumTotal + ":" + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx"+
                "?MerchantLogin="+merchantLogin+
                "&IndId="+invId+
                "&Culture=ru"+
                "&Encoding=utf-8"+
                "&OutSum="+paymentSumTotal+
                "&SignatureValue="+ DatatypeConverter.printHexBinary(md.digest()).toUpperCase()+
                "&IsTest=1";
    }
}
