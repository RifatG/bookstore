package com.example.my_book_shop_app.data_tests;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.book.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class BookRepositoryTests {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTests(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void findAllByIsBestseller() {
        List<Book> bestsellerBooks = bookRepository.getBestsellers();

        assertNotNull(bestsellerBooks);
        assertFalse(bestsellerBooks.isEmpty());

        for (Book book :
                bestsellerBooks) {
            assertEquals(1, book.getIsBestseller());
        }
    }

    @Test
    void findBooksByAuthorNameContaining() {
        String authorName = "Dredi";
        List<Book> bookListByAuthorName = bookRepository.findBooksByAuthorNameContaining(authorName);

        assertNotNull(bookListByAuthorName);
        assertFalse(bookListByAuthorName.isEmpty());

        for (Book book :
                bookListByAuthorName)
            assertEquals(authorName, book.getAuthor().getName());
    }

    @Test
    void findBooksByTitleContaining() {
        String title = "Simon";
        List<Book> bookListByTitle = bookRepository.findBooksByTitleContaining(title);

        assertNotNull(bookListByTitle);
        assertFalse(bookListByTitle.isEmpty());

        for (Book book :
                bookListByTitle) {
            assertTrue(book.getTitle().contains(title));
        }
    }
}