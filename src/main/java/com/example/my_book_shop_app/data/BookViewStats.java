package com.example.my_book_shop_app.data;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book_views_stats")
public class BookViewStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;
    private String bookSlug;
    private String bookTitle;
    private Long views = 0L;
    private Instant lastViewed;

    // constructors, getters, setters
    public Long getViews() {
        return views;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setBookSlug(String bookSlug) {
        this.bookSlug = bookSlug;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public void setLastViewed(Instant lastViewed) {
        this.lastViewed = lastViewed;
    }
}