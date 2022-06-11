package com.example.my_book_shop_app.security;

import com.example.my_book_shop_app.exceptions.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OauthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final BookstoreUserRegister userRegister;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public OauthAuthenticationSuccessHandler(BookstoreUserRegister userRegister, HandlerExceptionResolver handlerExceptionResolver) {
        this.userRegister = userRegister;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        try {
            userRegister.registerNewUser(user);
            response.sendRedirect("/my");
        } catch (UserAlreadyExistException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            response.sendRedirect("/signin");
        }
    }
}
