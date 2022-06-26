package com.example.my_book_shop_app.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.stream.Collectors;

public class BookListPage extends Page {

    public BookListPage(ChromeDriver driver) {
        super(driver);
    }

    public List<String> getBookListTitles() {
        List<WebElement> bookListTitlesElements =  driver.findElements(By.xpath("//strong[@class=\"Card-title\"]/a"));
        return bookListTitlesElements.stream().map(WebElement::getText).collect(Collectors.toList());
    }
}
