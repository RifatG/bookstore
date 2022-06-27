package com.example.my_book_shop_app.selenium.pages.genres;

public enum GenresEnum {
    COMEDY("Comedy"),
    DRAMA_THRILLER2("Drama|Thriller2"),
    DRAMA("Drama");

    private final String genreName;

    GenresEnum(String genre) {
        this.genreName = genre;
    }

    public String getGenreName() {
        return genreName;
    }
}
