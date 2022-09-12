package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.AuthorRepository;
import com.example.my_book_shop_app.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    private final ResourceStorage storage;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, ResourceStorage storage) {
        this.authorRepository = authorRepository;
        this.storage = storage;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy((Author a) -> a.getName().substring(0,1)));
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

    public Author createNewAuthor(String authorName, String description, MultipartFile photoFile) throws IOException {
        String slug = UUID.randomUUID().toString().substring(0, 10);
        String savePath = storage.saveNewAuthorImage(photoFile, slug);
        Author author = new Author();
        author.setName(authorName);
        author.setDescription(description);
        author.setSlug(slug);
        author.setPhoto(savePath);
        return authorRepository.save(author);
    }
}
