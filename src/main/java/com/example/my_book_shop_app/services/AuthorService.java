package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.AuthorRepository;
import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy((Author a) -> a.getName().substring(0,1).toUpperCase()));
    }
    public Author getAuthorById(int id) {
        return this.authorRepository.getAuthorById(id);
    }

    public boolean isThereAuthorWithName(String name) {
        return this.authorRepository.existsAuthorByName(name);
    }

    public void updateAuthor(Author author) {
        this.authorRepository.save(author);
    }

    public Author getAuthorByName(String name) {
        return this.authorRepository.getAuthorByName(name);
    }

    public Author createNewAuthor(String authorName, String description, String slug, String photo) {
        Author author = new Author();
        author.setName(authorName);
        author.setDescription(description);
        author.setSlug(slug);
        author.setPhoto(photo);
        return authorRepository.save(author);
    }

    public Author getAuthorBySlug(String slug) {
        return authorRepository.getAuthorBySlug(slug);
    }
}
