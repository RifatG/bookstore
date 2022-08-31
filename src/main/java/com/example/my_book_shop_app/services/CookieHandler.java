package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.aop.annotations.BookStatusCookieChangedLogger;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

@Service
public class CookieHandler {

    private static final String COOKIE_BOOKS_PATH = "/books";

    @BookStatusCookieChangedLogger
    public void updateSlugInCookie(String cookieValue, String cookieName, HttpServletResponse response, String slug) {
        if(cookieValue == null || cookieValue.equals("")) {
            Cookie cookie = new Cookie(cookieName, slug);
            cookie.setPath(COOKIE_BOOKS_PATH);
            response.addCookie(cookie);
        } else if(!cookieValue.contains(slug)){
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cookieValue).add(slug);
            Cookie cookie = new Cookie(cookieName, stringJoiner.toString());
            cookie.setPath(COOKIE_BOOKS_PATH);
            response.addCookie(cookie);
        }
    }

    @BookStatusCookieChangedLogger
    public void removeSlugFromCookie(String cookieValue, String cookieName, HttpServletResponse response, String slug) {
        if(cookieValue != null && !cookieValue.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cookieValue.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie(cookieName, String.join("/", cookieBooks));
            cookie.setPath(COOKIE_BOOKS_PATH);
            response.addCookie(cookie);
        }
    }

    public String[] getSlugsFromCookie(String cookieValue) {
        cookieValue = cookieValue.startsWith("/") ? cookieValue.substring(1) : cookieValue;
        cookieValue = cookieValue.endsWith("/") ? cookieValue.substring(0, cookieValue.length()-1) : cookieValue;
        return cookieValue.split("/");
    }

    public String getJwtTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void removeAllSlugs(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath(COOKIE_BOOKS_PATH);
        response.addCookie(cookie);
    }
}
