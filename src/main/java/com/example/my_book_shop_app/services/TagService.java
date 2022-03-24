package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.repositories.TagRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.tags.TagsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    @Autowired
    public TagService(TagRepository tagRepository, BookRepository bookRepository) {
        this.tagRepository = tagRepository;
        this.bookRepository = bookRepository;
    }

    public List<TagsEntity> getTagList() {
        return this.tagRepository.findAll();
    }

    public Page<Book> getPageOfBooksByTagId(int tagId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Integer> bookIdList = this.tagRepository.getBookIdListByTagId(tagId);
        return this.bookRepository.findAllByIdIn(bookIdList, nextPage);
    }

    public TagsEntity getTagById(int id) {
        return this.tagRepository.getTagsEntityById(id);
    }
}
