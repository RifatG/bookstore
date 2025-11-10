package com.example.my_book_shop_app.integration_tests;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.repositories.JwtBlacklistRepository;
import com.example.my_book_shop_app.security.BookstoreUserDetails;
import com.example.my_book_shop_app.security.jwt.JWTUtil;
import com.example.my_book_shop_app.struct.user.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class AuthenticationControllerTests {

    private final MockMvc mockMvc;
    private final JWTUtil jwtUtil;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final static String TEST_USERNAME = "Test";
    private final static String TEST_EMAIL = "test@test.test";
    private final static String TEST_PASSWORD = "123123";
    private final static String TEST_PHONE_NUMBER = "78888888888";
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationControllerTests(MockMvc mockMvc, JWTUtil jwtUtil, JwtBlacklistRepository jwtBlacklistRepository, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.jwtUtil = jwtUtil;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.objectMapper = objectMapper;
    }

    @Test
    void accessOnlyAuthorizedPageFailTest() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    void correctLoginFormTest() throws Exception {
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

    @Test
    void registrationNewUserTest() throws Exception {
        JSONObject registrationForm = new JSONObject();
        registrationForm.put("email", "new" + TEST_EMAIL);
        registrationForm.put("name", "new" + TEST_USERNAME);
        registrationForm.put("password", TEST_PASSWORD);
        registrationForm.put("phoneNumber", TEST_PHONE_NUMBER);
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationForm.toString()))
                .andDo(print())
                .andExpect(result -> assertNull(result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(true))
                .andExpect(status().isOk());
    }

    @Test
    void registrationAlreadyExistUserFailTest() throws Exception {
        JSONObject registrationForm = new JSONObject();
        registrationForm.put("email", TEST_EMAIL);
        registrationForm.put("name", TEST_USERNAME);
        registrationForm.put("password", TEST_PASSWORD);
        registrationForm.put("phoneNumber", TEST_PHONE_NUMBER);
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationForm.toString()))
                .andDo(print())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    ResultDto resultDto = objectMapper.readValue(body, ResultDto.class);
                    assertFalse(resultDto.isResult());
                    assertEquals(resultDto.getError(), "User with email " + TEST_EMAIL + " is already signed up");
                })
                .andExpect(status().isOk());
    }

    @Test
    void correctLoginTest() throws Exception {
        JSONObject credentials = new JSONObject();
        credentials.put("contact", TEST_EMAIL);
        credentials.put("code", TEST_PASSWORD);
        mockMvc.perform(post("/login-by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials.toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").doesNotExist())
                .andExpect(cookie().exists("token"));
    }

    @Test
    void incorrectEmailLoginFailTest() throws Exception {
        JSONObject credentials = new JSONObject();
        credentials.put("contact", "fake@email.com");
        credentials.put("code", TEST_PASSWORD);
        mockMvc.perform(post("/login-by-email")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(credentials.toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").isString())
                .andExpect(cookie().doesNotExist("token"));
    }

    @Test
    void incorrectPasswordLoginFailTest() throws Exception {
        JSONObject credentials = new JSONObject();
        credentials.put("contact", TEST_EMAIL);
        credentials.put("code", "fake_password");
        mockMvc.perform(post("/login-by-email")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(credentials.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andExpect(cookie().doesNotExist("token"));
    }

    @Test
    @WithUserDetails(TEST_USERNAME)
    @Transactional
    void logoutTest() throws Exception {
        UserEntity user = new UserEntity();
        user.setName(TEST_USERNAME);
        String token = jwtUtil.generateToken(new BookstoreUserDetails(user));
        Cookie cookie = new Cookie("token", token);
        mockMvc.perform(get("/logout").cookie(cookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"))
                .andExpect(result -> {
                    Cookie resCookie = result.getResponse().getCookie("token");
                    assertNotNull(resCookie);
                    assertNull(resCookie.getValue());
                });

        assertNotNull(jwtBlacklistRepository.findByToken(token));
    }
}
