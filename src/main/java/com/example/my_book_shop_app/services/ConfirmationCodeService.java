package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.exceptions.ConfirmationCodeException;
import com.example.my_book_shop_app.repositories.ConfirmationCodeRepository;
import com.example.my_book_shop_app.struct.external_api.ConfirmationCode;
import com.example.my_book_shop_app.struct.external_api.ConfirmationCodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ConfirmationCodeService {

    @Value("${sms_ru.account_id}")
    private String accountId;

    @Value("${app-email.email}")
    private String codeSendingEmail;

    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final RestTemplate restClient;
    private final Logger logger = LoggerFactory.getLogger(ConfirmationCodeService.class);
    private final Random random;
    private final JavaMailSender javaMailSender;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository smsCodeRepository, RestTemplate restClient, JavaMailSender javaMailSender) throws NoSuchAlgorithmException {
        this.confirmationCodeRepository = smsCodeRepository;
        this.javaMailSender = javaMailSender;
        this.restClient = restClient;
        this.restClient.setMessageConverters(getConvertersForRestTemplate());
        random = SecureRandom.getInstanceStrong();
    }

    public String sendPhoneConfirmationCode(String contact) throws ConfirmationCodeException {
        String uri = String.format("https://sms.ru/code/call?phone=%s&ip=33.22.11.55&api_id=%s", contact.replaceAll("\\D+", ""), accountId);
        ConfirmationCodeResponse response = restClient.getForObject(uri, ConfirmationCodeResponse.class);
        if (response != null) {
            if(response.getStatus().equals("OK")) {
                String code = response.getCode().toString();
                code = code.substring(0, 2)  + " " + code.substring(2);
                logger.info("Confirmation code {} has been sent to phone number: {}", code, contact);
                return code;
            } else throw new ConfirmationCodeException(response.getStatus_text());
        } else return null;
    }

    public String sendEmailConfirmationCode(String contact) throws ConfirmationCodeException {
        SimpleMailMessage message = new SimpleMailMessage();
        String confirmationCode = generateConfirmationCode();
        message.setFrom(codeSendingEmail);
        message.setTo(contact);
        message.setSubject("Bookstore email verification");
        message.setText("Verification code is: " + confirmationCode);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new ConfirmationCodeException("Confirmation code wasn't sent. Error: " + e.getMessage());
        }
        return confirmationCode;
    }

    public void saveNewConfirmationCode(ConfirmationCode code) {
        if (confirmationCodeRepository.findByCode(code.getCode()) == null) {
            confirmationCodeRepository.save(code);
        }
    }

    public Boolean verifyConfirmationCode(String code) {
        ConfirmationCode confirmationCode = confirmationCodeRepository.findByCode(code);
        return confirmationCode != null && !confirmationCode.isExpired();
    }

    private List<HttpMessageConverter<?>> getConvertersForRestTemplate() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }

    private String generateConfirmationCode() {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(random.nextInt(9));
        }
        sb.insert(3, " ");
        return sb.toString();
    }
}

