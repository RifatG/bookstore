package com.example.my_book_shop_app.selenium.pages.genres;

import com.example.my_book_shop_app.selenium.pages.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GenresPage extends Page {

    public GenresPage(ChromeDriver driver) {
        super(driver);
    }

    public GenrePage goToGenre(GenresEnum genre) {
        WebElement genreElement = driver.findElement(By.xpath("//*[contains(@class,\"Tags_genres\")]//div[@class=\"Tag\"]//span[contains(text(),\"" + genre.getGenreName() + " \")]"));
        genreElement.click();
        return new GenrePage(driver);
    }
}
