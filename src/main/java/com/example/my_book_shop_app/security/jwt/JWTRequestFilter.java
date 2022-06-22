package com.example.my_book_shop_app.security.jwt;

import com.example.my_book_shop_app.exceptions.EmptyJwtTokenException;
import com.example.my_book_shop_app.exceptions.JwtInBlacklistException;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.BookstoreUserDetailsService;
import com.example.my_book_shop_app.services.JwtBlacklistService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtBlacklistService jwtBlacklistService;

    @Autowired
    public JWTRequestFilter(BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil, HandlerExceptionResolver handlerExceptionResolver, JwtBlacklistService jwtBlacklistService) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException, AuthenticationException {
        String token = null;
        String username = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        try {
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                        if (token == null || token.equals("")) throw new EmptyJwtTokenException("Jwt Token cannot be null of empty");
                        if (jwtBlacklistService.isTokenInBlacklist(token)) throw new JwtInBlacklistException("Current token is in blacklist");
                        username = jwtUtil.extractUsername(token);
                    }

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(username);
                        if (Boolean.TRUE.equals(jwtUtil.validateToken(token, userDetails))) {
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (JwtException | JwtInBlacklistException | EmptyJwtTokenException | UsernameNotFoundException e) {
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
            httpServletResponse.sendRedirect("signin");
        }
    }
}
