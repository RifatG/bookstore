package com.example.my_book_shop_app.struct.book;

import com.example.my_book_shop_app.struct.author.Author;
import com.example.my_book_shop_app.struct.tags.TagsEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "books")
@ApiModel(description = "entity representation a book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id generated by db automatically")
    private int id;

    @Column(columnDefinition = "DATE NOT NULL")
    @ApiModelProperty("date of book publication")
    private Date pubDate;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    @ApiModelProperty("if isBestseller = 1 so the book is considered to be bestseller and if 0 the book is not a bestseller")
    private byte isBestseller;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @ApiModelProperty("mnemonical identity sequence of characters")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @ApiModelProperty("book title")
    private String title;

    @Column(columnDefinition = "VARCHAR(255)")
    @ApiModelProperty("book url")
    private String image;

    @Column(columnDefinition = "TEXT")
    @ApiModelProperty("book description text")
    private String description;

    @Column(columnDefinition = "INT NOT NULL")
    @ApiModelProperty("book price")
    private int price;

    @Column(columnDefinition = "SMALLINT NOT NULL DEFAULT 0")
    @ApiModelProperty("discount value for book")
    private byte discount;

    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer paidCount;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer inCartCount;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer keptCount;

    @ManyToMany
    @JoinTable(
            name = "book2Tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnore
    private List<TagsEntity> tagList;

    @ManyToMany
    @JoinTable(
            name = "book2genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @JsonIgnore
    private List<TagsEntity> genreList;

    public Integer getPaidCount() {
        return paidCount;
    }

    public void setPaidCount(Integer paidCount) {
        this.paidCount = paidCount;
    }

    public Integer getInCartCount() {
        return inCartCount;
    }

    public void setInCartCount(Integer inCartCount) {
        this.inCartCount = inCartCount;
    }

    public Integer getKeptCount() {
        return keptCount;
    }

    public void setKeptCount(Integer keptCount) {
        this.keptCount = keptCount;
    }

    public int getPriceOld() {
        return (discount != 0 && discount != 100) ? (100 * price)/(100 - discount) : 0;
    }

    public byte isBestseller() {
        return isBestseller;
    }

    public void setBestseller(byte bestseller) {
        isBestseller = bestseller;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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

    public List<TagsEntity> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagsEntity> tagList) {
        this.tagList = tagList;
    }

    public List<TagsEntity> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<TagsEntity> genreList) {
        this.genreList = genreList;
    }
}