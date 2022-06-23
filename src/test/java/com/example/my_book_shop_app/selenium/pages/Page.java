package com.example.my_book_shop_app.selenium.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;

public class Page {

    protected final ChromeDriver driver;

    @Value("${server.port}")
    private Integer port;

    protected final String BASE_URL = "http://localhost:8085";

    public Page(ChromeDriver driver) {
        this.driver = driver;
    }

    public Page setUpSearchTokenToSearchInput(String token) {
        WebElement searchInput = driver.findElementById("query");
        searchInput.sendKeys(token);
        return this;
    }

    public Page clickSearchButton() {
        WebElement searchButton = driver.findElementById("search");
        searchButton.submit();
        return new SearchPage(driver);
    }

    public Page pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }
}
