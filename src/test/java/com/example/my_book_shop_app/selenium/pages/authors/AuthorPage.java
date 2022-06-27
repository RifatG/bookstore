package com.example.my_book_shop_app.selenium.pages.authors;

import com.example.my_book_shop_app.selenium.pages.BookListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class AuthorPage extends BookListPage {

    public AuthorPage(ChromeDriver driver) {
        super(driver);
    }

    public WebElement getAuthorBlockTitle() {
        return this.driver.findElement(By.xpath("//h1[@class=\"Middle-title\"]"));
    }
}
