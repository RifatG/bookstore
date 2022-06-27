package com.example.my_book_shop_app.selenium.pages;

import com.example.my_book_shop_app.selenium.pages.authors.AuthorsPage;
import com.example.my_book_shop_app.selenium.pages.genres.GenresPage;
import com.example.my_book_shop_app.selenium.pages.news.NewsPage;
import com.example.my_book_shop_app.selenium.pages.popular.PopularPage;
import org.openqa.selenium.By;
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

    public GenresPage goToGenres() {
        WebElement genres = driver.findElementById("navigate").findElement(By.xpath("//li[2]"));
        genres.click();
        return new GenresPage(driver);
    }

    public NewsPage goToNews() {
        WebElement genres = driver.findElementById("navigate").findElement(By.xpath("//li[3]"));
        genres.click();
        return new NewsPage(driver);
    }

    public PopularPage goToPopular() {
        WebElement genres = driver.findElementById("navigate").findElement(By.xpath("//li[4]"));
        genres.click();
        return new PopularPage(driver);
    }

    public AuthorsPage goToAuthors() {
        WebElement genres = driver.findElementById("navigate").findElement(By.xpath("//li[5]"));
        genres.click();
        return new AuthorsPage(driver);
    }

    public MainPage goToMain() {
        WebElement genres = driver.findElementById("navigate").findElement(By.xpath("//li[1]"));
        genres.click();
        return new MainPage(driver);
    }
}
