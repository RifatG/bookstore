package com.example.my_book_shop_app.data;

public class ResultDto {

    private boolean result;
    private String error;

    public ResultDto(boolean result) {
        this.result = result;
    }

    public ResultDto(boolean result, String error) {
        this.result = result;
        this.error = error;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
