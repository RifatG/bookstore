package com.example.my_book_shop_app.selenium.pages;

import org.openqa.selenium.chrome.ChromeDriver;

public class SearchPage extends Page {

    private final String MAIN_PAGE_URL = BASE_URL.concat("/search/");

    public SearchPage callPage(String searchToken) {
        this.driver.get(MAIN_PAGE_URL + searchToken);
        return this;
    }

    public SearchPage(ChromeDriver driver) {
        super(driver);
    }
}
