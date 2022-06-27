package com.example.my_book_shop_app.selenium.configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseConfiguration {

    protected ChromeDriver driver;

    private static final String CHROME_DRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";

    @Value("${webdriver.chrome.driver}")
    private String CHROME_DRIVER_PATH;

    @BeforeAll
    void setUp() {
        System.setProperty(CHROME_DRIVER_SYSTEM_PROPERTY, CHROME_DRIVER_PATH);
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @AfterAll
    void tearDown() {
        driver.quit();
    }
}
