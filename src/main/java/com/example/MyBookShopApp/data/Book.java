package com.example.MyBookShopApp.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Book {

    private Integer id;
    private String author;
    private String title;
    private String priceOld;
    private String price;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", priceOld='" + priceOld + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(String priceOld) {
        this.priceOld = priceOld;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getDiscount() {
        if (this.priceOld != null) {
            Pattern pattern = Pattern.compile("([0-9]+[.]*[0-9]*)");
            Matcher priceMatcher = pattern.matcher(this.price);
            Matcher priceOldMatcher = pattern.matcher(this.priceOld);
            if(priceMatcher.find() && priceOldMatcher.find()) {
                return (int) ((Double.parseDouble(priceMatcher.group())/Double.parseDouble(priceOldMatcher.group())) * 100 - 100);
            }
        }
        return 0;
    }
}
