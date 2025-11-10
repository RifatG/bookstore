package com.example.my_book_shop_app.struct.book;

import com.example.my_book_shop_app.struct.author.Author;
import com.example.my_book_shop_app.struct.book.file.BookFile;
import com.example.my_book_shop_app.struct.book.links.Book2UserTypeEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.genre.GenreEntity;
import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.tags.TagsEntity;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "DATE NOT NULL")
    private Date pubDate;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private byte isBestseller;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    @Column(columnDefinition = "VARCHAR(255)")
    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "INT NOT NULL")
    private int price;

    @Column(columnDefinition = "SMALLINT NOT NULL DEFAULT 0")
    private byte discount;

    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @JsonGetter("authors")
    public String authorsFullName() {
        return this.author.toString();
    }

    @ManyToMany
    @JoinTable(
            name = "book2user",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id"))
    @JsonIgnore
    private List<Book2UserTypeEntity> userRelations;

    @ManyToMany
    @JoinTable(
            name = "book2genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @JsonIgnore
    private List<GenreEntity> genreList;

    @ManyToMany
    @JoinTable(
            name = "book2Tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnore
    private List<TagsEntity> tagList;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookFile> bookFileList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RatingEntity> ratingList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BookReviewEntity> reviewList = new ArrayList<>();

    @JsonProperty
    public Integer getDiscountPrice() {
        return price - Math.toIntExact(Math.round(price * ((double)discount / 100)));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public byte getIsBestseller() {
        return isBestseller;
    }

    public void setIsBestseller(byte isBestseller) {
        this.isBestseller = isBestseller;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public byte getDiscount() {
        return discount;
    }

    public void setDiscount(byte discount) {
        this.discount = discount;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Book2UserTypeEntity> getUserRelations() {
        return userRelations;
    }

    public void setUserRelations(List<Book2UserTypeEntity> userRelations) {
        this.userRelations = userRelations;
    }

    public List<GenreEntity> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<GenreEntity> genreList) {
        this.genreList = genreList;
    }

    public List<TagsEntity> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagsEntity> tagList) {
        this.tagList = tagList;
    }

    public List<BookFile> getBookFileList() {
        return bookFileList;
    }

    public void setBookFileList(List<BookFile> bookFileList) {
        this.bookFileList = bookFileList;
    }

    public List<RatingEntity> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<RatingEntity> ratingList) {
        this.ratingList = ratingList;
    }

    public List<BookReviewEntity> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<BookReviewEntity> reviewList) {
        this.reviewList = reviewList;
    }
}