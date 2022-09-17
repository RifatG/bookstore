package com.example.my_book_shop_app.selenium.specs;

import com.example.my_book_shop_app.selenium.configuration.BaseConfiguration;
import com.example.my_book_shop_app.selenium.pages.BookPage;
import com.example.my_book_shop_app.selenium.pages.authors.AuthorPage;
import com.example.my_book_shop_app.selenium.pages.genres.GenrePage;
import com.example.my_book_shop_app.selenium.pages.genres.GenresEnum;
import com.example.my_book_shop_app.selenium.pages.MainPage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@SpringBootTest
class NavigationTests extends BaseConfiguration {

    @Test
    void navigationTest() {
        MainPage mainPage = new MainPage(driver);
        mainPage.callPage();
        assertTrue(mainPage.getRecommendedBlockTitle().isDisplayed());

        GenrePage genrePage = mainPage
                .goToGenres()
                .goToGenre(GenresEnum.DRAMA_THRILLER2);
        assertTrue(genrePage.getBookListTitles().contains("Interiors"));

        AuthorPage authorPage = genrePage
                .goToNews()
                .goToPopular()
                .goToAuthors()
                .goToAuthor("Alina");
        assertEquals("Alina", authorPage.getAuthorBlockTitle().getText());

        BookPage bookPage = authorPage
                .goToMain()
                .goToBook("Simon Says");
        assertEquals("Simon Says", bookPage.getBookTitle().getText());
    }
}
