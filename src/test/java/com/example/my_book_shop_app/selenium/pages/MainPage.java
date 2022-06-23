package com.example.my_book_shop_app.selenium.pages;

import org.openqa.selenium.chrome.ChromeDriver;

public class MainPage extends Page {

    private final String MAIN_PAGE_URL = BASE_URL.concat("/");

    public MainPage(ChromeDriver driver) {
        super(driver);
    }

    public MainPage callPage() {
        this.driver.get(MAIN_PAGE_URL);
        return this;
    }
}
