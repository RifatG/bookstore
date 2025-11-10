package com.example.my_book_shop_app.struct.book.review;

import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_review")
public class BookReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToMany(mappedBy = "reviewEntity")
    @JsonIgnore
    private List<BookReviewLikeEntity> reviewLikeList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<BookReviewLikeEntity> getReviewLikeList() {
        return reviewLikeList;
    }

    public void setReviewLikeList(List<BookReviewLikeEntity> reviewLikeList) {
        this.reviewLikeList = reviewLikeList;
    }

    public Integer getLikes() {
        return (int) this.reviewLikeList.stream().filter(i -> i.getValue() == 1).count();
    }

    public Integer getDislikes() {
        return (int) this.reviewLikeList.stream().filter(i -> i.getValue() == -1).count();
    }

    public Integer getRating() {
        int likes = this.getLikes();
        int dislikes = this.getDislikes();
        if (likes + dislikes == 0) return 0;
        return likes * 100 / (dislikes + likes);
    }
}
