package com.example.my_book_shop_app.data;

import javax.persistence.*;

@MappedSuperclass
public abstract class FilteredBook {

    @Id
    @Column(name = "book_id")
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "book_id")
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}