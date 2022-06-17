package com.example.my_book_shop_app.properties_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationPropertiesTest {

    @Value("${server.port}")
    private Integer serverPort;

    @Test
    void verifyServerPortTest() {
        assertEquals(8085, serverPort);
    }
}
