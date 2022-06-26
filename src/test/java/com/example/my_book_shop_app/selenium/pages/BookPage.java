package com.example.my_book_shop_app.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BookPage extends Page {

    public BookPage(ChromeDriver driver) {
        super(driver);
    }

    public WebElement getBookTitle() {
        return this.driver.findElement(By.xpath("//h1[@class=\"ProductCard-title\"]"));
    }
}
