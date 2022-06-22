package com.example.my_book_shop_app.data;

public class ContactConfirmationResponse {

    private Boolean result;
    private String token;
    private String error;

    public ContactConfirmationResponse(Boolean result) {
        this.result = result;
    }

    public ContactConfirmationResponse(Boolean result, String token) {
        this.result = result;
        this.token = token;
    }

    public ContactConfirmationResponse(String error) {
        this.result = false;
        this.error = error;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
