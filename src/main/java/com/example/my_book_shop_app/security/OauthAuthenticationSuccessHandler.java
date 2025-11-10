package com.example.my_book_shop_app.security;

import com.example.my_book_shop_app.exceptions.UserAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OauthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OauthAuthenticationSuccessHandler.class);

    private final BookstoreUserRegister userRegister;

    @Autowired
    public OauthAuthenticationSuccessHandler(BookstoreUserRegister userRegister) {
        this.userRegister = userRegister;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        try {
            userRegister.registerNewUser(user);
            logger.info("New user has been registered while authentication via oauth2. User has successfully signed up!");
        } catch (UserAlreadyExistException e) {
            logger.info("User is already exist while authentication via oauth2. User has successfully signed up!");
        }
        response.sendRedirect("/my");
    }
}
