package com.example.my_book_shop_app.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class MainPageControllerTest {

    private final MockMvc mockMvc;
    private final static String TEST_USERNAME = "Test";
    private final static String TEST_EMAIL = "test@test.test";
    private final static String TEST_PASSWORD = "123123";

    @Autowired
    MainPageControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void mainPageAccessTest() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(content().string(containsString("")))
                .andExpect(status().isOk());
    }

    @Test
    void accessOnlyAuthorizedPageFailTest() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    void correctLoginTest() throws Exception {
        mockMvc.perform(formLogin("/signin").user(TEST_USERNAME).password(TEST_PASSWORD))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails(TEST_USERNAME)
    @Transactional
    void authenticatedAccessToProfilePageTest() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//span[@class='CartBlock-text']").string(TEST_USERNAME));
    }
}