package com.example.my_book_shop_app.data_tests;

import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void addNewUserTest() {
        UserEntity user = new UserEntity();
        user.setName("Tester");
        user.setRegTime(LocalDateTime.now());
        user.setHash("hash");
        user.setBalance(0d);
        user.setPassword("password");

        assertNotNull(userRepository.save(user));
    }
}
