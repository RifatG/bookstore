package com.example.my_book_shop_app.selenium.pages;

import com.example.my_book_shop_app.selenium.pages.authors.AuthorPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

    public WebElement getRecommendedBlockTitle() {
        return this.driver.findElement(By.xpath("//*[@class=\"Section-title\"][text()=\"Recommended\"]"));
    }

    public BookPage goToBook(String bookName) {
        WebElement bookElement = driver.findElement(By.xpath("//strong[@class=\"Card-title\"]/a[text()=\"" + bookName + "\"]"));
        bookElement.click();
        return new BookPage(driver);
    }
}
