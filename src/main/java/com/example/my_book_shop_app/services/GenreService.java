package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.repositories.GenreRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.genre.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
    }

    public List<GenreEntity> getGenreList() {
        List<GenreEntity> genres = this.genreRepository.findAll();
        genres.forEach(genre -> genres.forEach(g -> {
            if(genre.getParentId() != null && g.getId() == genre.getParentId()) g.setChild(true);
        }));
        return genres;
    }

    public Page<Book> getPageOfBooksByGenreId(int genreId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Integer> bookIdList = this.genreRepository.getBookIdListByGenreId(genreId);
        return this.bookRepository.findAllByIdIn(bookIdList, nextPage);
    }

    public GenreEntity getGenreById(int id) {
        return this.genreRepository.getGenreEntityById(id);
    }
}
