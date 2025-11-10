package com.example.my_book_shop_app.exceptions;

import com.example.my_book_shop_app.data.ApiResponse;
import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.struct.book.Book;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@ControllerAdvice
public class ExceptionHandlerController {

    Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);
    private static final String JWT_EXCEPTION_LOG_STRING = "JWT token exception: {}";
    private static final String REDIRECT_SIGN_IN_PAGE_STRING = "redirect:/signin";

    @ExceptionHandler(BookStoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse<Book>> handleBookStoreApiWrongParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad parameter value...", exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Book>> handleMissingServletRequestParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing required parameters", exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptySearchException.class)
    public String handleEmptySearchException(EmptySearchException e, RedirectAttributes redirectAttributes) {
        java.util.logging.Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        redirectAttributes.addFlashAttribute("searchError", e);
        return "redirect:/";
    }

    @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException e, HttpServletResponse response){
        logger.error(JWT_EXCEPTION_LOG_STRING, e.getMessage());
        clearCookieAndContext(response);
        return REDIRECT_SIGN_IN_PAGE_STRING;
    }

    @ExceptionHandler(JwtInBlacklistException.class)
    public String handleJwtInBlacklistException(JwtInBlacklistException e, HttpServletResponse response){
        logger.error(JWT_EXCEPTION_LOG_STRING, e.getMessage());
        clearCookieAndContext(response);
        return REDIRECT_SIGN_IN_PAGE_STRING;
    }

    @ExceptionHandler(EmptyJwtTokenException.class)
    public String handleEmptyJwtTokenException(EmptyJwtTokenException e, HttpServletResponse response){
        logger.error(JWT_EXCEPTION_LOG_STRING, e.getMessage());
        clearCookieAndContext(response);
        return REDIRECT_SIGN_IN_PAGE_STRING;
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResultDto handleUserAlreadyExistException(UserAlreadyExistException e, HttpServletResponse response){
        logger.error("Registration exception: {}", e.getMessage());
        clearCookieAndContext(response);
        return new ResultDto(false, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletResponse response){
        logger.error("Registration exception: {}", e.getMessage());
        clearCookieAndContext(response);
        return REDIRECT_SIGN_IN_PAGE_STRING;
    }

    private void clearCookieAndContext(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        SecurityContextHolder.clearContext();
        logger.info("Cookie has been cleaned. Security context has been cleaned.");
    }
}
