package com.example.my_book_shop_app.unit_tests.authors_tests;

import com.example.my_book_shop_app.repositories.AuthorRepository;
import com.example.my_book_shop_app.services.AuthorService;
import com.example.my_book_shop_app.struct.author.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthorServiceTests {

    private final AuthorService authorService;
    private List<Author> authorsMock;
    private Map<String, List<Author>> expectedAuthorsMap;

    @MockBean
    private AuthorRepository authorRepositoryMock;

    @Autowired
    public AuthorServiceTests(AuthorService authorService) {
        this.authorService = authorService;
    }

    @BeforeEach
    void setUp() {
        authorsMock = Arrays.asList(new Author(), new Author(), new Author(), new Author());
        authorsMock.get(0).setName("George Orwell");
        authorsMock.get(1).setName("Alexander Bronson");
        authorsMock.get(2).setName("JK Rowling");
        authorsMock.get(3).setName("Arthur");
        Mockito.doReturn(authorsMock).when(authorRepositoryMock).findAll();
        expectedAuthorsMap = new HashMap<>();
        expectedAuthorsMap.put("A", Arrays.asList(authorsMock.get(1), authorsMock.get(3)));
        expectedAuthorsMap.put("G", Collections.singletonList(authorsMock.get(0)));
        expectedAuthorsMap.put("J", Collections.singletonList(authorsMock.get(2)));
    }

    @AfterEach
    void tearDown() {
        authorsMock = null;
    }

    @Test
    void getAuthorsMapTest() {
        Map<String, List<Author>> authors = authorService.getAuthorsMap();
        assertEquals(3, authors.size());
        assertEquals(authors, expectedAuthorsMap);
        Mockito.verify(authorRepositoryMock, Mockito.times(1))
                .findAll();
    }
}
