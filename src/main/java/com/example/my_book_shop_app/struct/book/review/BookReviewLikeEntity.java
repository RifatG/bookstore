package com.example.my_book_shop_app.struct.book.review;

import com.example.my_book_shop_app.struct.user.UserEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_review_like")
public class BookReviewLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "INT NOT NULL")
    private short value;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private BookReviewEntity reviewEntity;

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

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public BookReviewEntity getReviewEntity() {
        return reviewEntity;
    }

    public void setReviewEntity(BookReviewEntity reviewEntity) {
        this.reviewEntity = reviewEntity;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
