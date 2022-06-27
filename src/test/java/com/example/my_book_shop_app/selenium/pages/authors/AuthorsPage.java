package com.example.my_book_shop_app.selenium.pages.authors;

import com.example.my_book_shop_app.selenium.pages.Page;
import com.example.my_book_shop_app.selenium.pages.genres.GenrePage;
import com.example.my_book_shop_app.selenium.pages.genres.GenresEnum;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class AuthorsPage extends Page {

    public AuthorsPage(ChromeDriver driver) {
        super(driver);
    }

    public AuthorPage goToAuthor(String authorName) {
        char firstLetterOfAuthorName = authorName.toLowerCase().charAt(0);
        WebElement authorElement = driver.findElement(By.xpath("//h2[@class=\"Authors-title\"][@id=\"" + firstLetterOfAuthorName + "\"]/..//a[text()=\"" + authorName + "\"]"));
        authorElement.click();
        return new AuthorPage(driver);
    }
}
