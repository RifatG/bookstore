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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ConfirmationCodeService {

    @Value("${sms_ru.account_id}")
    private String accountId;

    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final RestTemplate restClient;
    private final Logger logger = LoggerFactory.getLogger(ConfirmationCodeService.class);

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository smsCodeRepository) {
        this.confirmationCodeRepository = smsCodeRepository;
        this.restClient = new RestTemplate();
        restClient.setMessageConverters(getConvertersForRestTemplate());
    }

    public String sendConfirmationCode(String contact) throws ConfirmationCodeException {
        String uri = String.format("https://sms.ru/code/call?phone=%s&ip=33.22.11.55&api_id=%s", contact.replaceAll("\\D+", ""), accountId);
        ConfirmationCodeResponse response = restClient.getForObject(uri, ConfirmationCodeResponse.class);
        if (response != null) {
            if(response.getStatus().equals("OK")) {
                logger.info("Confirmation code {} has been sent to phone number: {}", response.getCode(), contact);
                return response.getCode().toString();
            } else throw new ConfirmationCodeException(response.getStatus_text());
        } else return null;
    }

    public void saveNewConfirmationCode(ConfirmationCode code) {
        if (confirmationCodeRepository.findByCode(code.getCode()) == null) {
            confirmationCodeRepository.save(code);
        }
    }

    public Boolean verifyConfirmationCode(String code) {
        ConfirmationCode confirmationCode = confirmationCodeRepository.findByCode(code.replace(" ", ""));
        return confirmationCode != null && !confirmationCode.isExpired();
    }

    private List<HttpMessageConverter<?>> getConvertersForRestTemplate() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }
}

