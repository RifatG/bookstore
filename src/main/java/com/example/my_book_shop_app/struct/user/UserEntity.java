package com.example.my_book_shop_app.struct.user;

import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private int balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BookReviewEntity> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BookReviewLikeEntity> reviewLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<RatingEntity> ratingList = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private UserContactEntity userContact;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;
}
