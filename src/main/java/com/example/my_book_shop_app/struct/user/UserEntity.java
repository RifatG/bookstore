package com.example.my_book_shop_app.struct.user;

import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.example.my_book_shop_app.struct.enums.ContactType;
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
    private Double balance;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserContactEntity> userContacts;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;

    @ManyToOne
    @JoinTable(
            name = "user2roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Role role;

    public UserContactEntity getEmailContact() {
        return this.userContacts.stream().filter(contact -> contact.getType().equals(ContactType.EMAIL)).findFirst().orElse(null);
    }

    public UserContactEntity getPhoneContact() {
        return this.userContacts.stream().filter(contact -> contact.getType().equals(ContactType.PHONE)).findFirst().orElse(null);
    }
}
