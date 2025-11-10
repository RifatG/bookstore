package com.example.my_book_shop_app.aop.aspects;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class BookStatusCookiesLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(BookStatusCookiesLoggerAspect.class);

    @Pointcut(value = "@annotation(com.example.my_book_shop_app.aop.annotations.BookStatusCookieChangedLogger)")
    public void cookieChangedPointcut() {
        //pointcut
    }

    @After(value = "args(cookieValue, cookieName, response, slug) && cookieChangedPointcut()", argNames = "cookieValue,cookieName,response,slug")
    public void cookieChangedAdvice(String cookieValue, String cookieName, HttpServletResponse response, String slug) {
        logger.info("Cookie {} has been changed", cookieName);
    }
}
