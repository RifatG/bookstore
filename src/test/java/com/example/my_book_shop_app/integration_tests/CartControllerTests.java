package com.example.my_book_shop_app.integration_tests;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class CartControllerTests {

    private final MockMvc mockMvc;

    private static final String CART_CONTENTS_COOKIE = "cartContents";
    private static final String BOOK_CART_STATUS = "CART";
    private static final String BOOK_SLUG = "604095584-8";

    @Autowired
    public CartControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void addBookToCartTest() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("booksIds", 1);
        payload.put("status", BOOK_CART_STATUS);
        mockMvc.perform(post("/books/changeBookStatus/" + BOOK_SLUG)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload.toString()))
                .andDo(print())
                .andExpect(cookie().value(CART_CONTENTS_COOKIE, BOOK_SLUG))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/book/" + BOOK_SLUG));
    }

    @Test
    void addAlreadyAddedBookToCartTest() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("booksIds", 1);
        payload.put("status", BOOK_CART_STATUS);
        Cookie cookie = new Cookie(CART_CONTENTS_COOKIE, BOOK_SLUG);
        mockMvc.perform(post("/books/changeBookStatus/" + BOOK_SLUG)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload.toString())
                        .cookie(cookie))
                .andDo(print())
                .andExpect(cookie().doesNotExist(CART_CONTENTS_COOKIE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/book/" + BOOK_SLUG));
    }

    @Test
    void deleteBookFromCartTest() throws Exception {
        Cookie cookie = new Cookie(CART_CONTENTS_COOKIE, BOOK_SLUG);
        mockMvc.perform(post("/books/changeBookStatus/cart/remove/" + BOOK_SLUG).cookie(cookie))
                .andExpect(cookie().value(CART_CONTENTS_COOKIE, ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/cart"));
    }

    @Test
    void deleteBookFromCartFailTest() throws Exception {
        mockMvc.perform(post("/books/changeBookStatus/cart/remove/" + BOOK_SLUG))
                .andExpect(cookie().doesNotExist(CART_CONTENTS_COOKIE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/cart"));
    }
}
