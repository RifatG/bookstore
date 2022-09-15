package com.example.my_book_shop_app.security;

import com.example.my_book_shop_app.security.jwt.JWTRequestFilter;
import com.example.my_book_shop_app.services.CookieHandler;
import com.example.my_book_shop_app.services.JwtBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String SIGN_IN_URL = "/signin";
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTRequestFilter filter;
    private final CookieHandler cookieHandler;
    private final JwtBlacklistService jwtBlacklistService;
    private final OauthAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService, JWTRequestFilter filter, CookieHandler cookieHandler, JwtBlacklistService jwtBlacklistService, OauthAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.filter = filter;
        this.cookieHandler = cookieHandler;
        this.jwtBlacklistService = jwtBlacklistService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile", "/books/viewed").authenticated()
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage(SIGN_IN_URL).failureUrl(SIGN_IN_URL)
                .and().logout().logoutUrl("/logout")
                .addLogoutHandler(((request, response, authentication) -> {
                    String token = cookieHandler.getJwtTokenFromCookie(request);
                    if(token != null) jwtBlacklistService.addToBlacklist(token);
                }))
                .logoutSuccessUrl(SIGN_IN_URL).deleteCookies("token")
                .and().oauth2Login().successHandler(customAuthenticationSuccessHandler)
                .and().oauth2Client();
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
