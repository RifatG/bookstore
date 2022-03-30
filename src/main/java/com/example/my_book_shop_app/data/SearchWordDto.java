package com.example.my_book_shop_app.data;

public class SearchWordDto {

    private String example;

    public SearchWordDto(String example) {
        this.example = example;
    }

    public SearchWordDto() {}

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
