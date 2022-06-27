package com.example.my_book_shop_app.unit_tests.properties_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class ApplicationPropertiesTests {

    @Value("${server.port}")
    private Integer serverPort;

    @Test
    void verifyServerPortTest() {
        assertEquals(8085, serverPort);
    }
}
