package com.example.my_book_shop_app.selenium.specs;

import com.example.my_book_shop_app.selenium.configuration.BaseConfiguration;
import com.example.my_book_shop_app.selenium.pages.MainPage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
class MainPageTests extends BaseConfiguration {

    @Test
    void mainPageAccessTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    void searchBookTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .setUpSearchTokenToSearchInput("Simon")
                .clickSearchButton()
                .pause();

        assertTrue(driver.getPageSource().contains("Simon Says"));
    }
}
