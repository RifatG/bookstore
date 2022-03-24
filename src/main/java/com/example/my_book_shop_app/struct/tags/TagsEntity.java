package com.example.my_book_shop_app.struct.tags;

import com.example.my_book_shop_app.struct.book.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tags")
public class TagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @ManyToMany(mappedBy = "tagList")
    @JsonIgnore
    private List<Book> bookList;

    public String getTagSize() {
        int size = this.bookList.size();
        if (size < 60 ) return "Tag_xs";
        if (size < 65) return "Tag_sm";
        if (size < 75) return "Tag_md";
        return "Tag_lg";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    @Override
    public String toString() {
        return name;
    }
}
