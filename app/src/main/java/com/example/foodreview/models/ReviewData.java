package com.example.foodreview.models;

public class ReviewData {

    String user;
    String text;
    String placeId;

    public ReviewData(String user, String text, String placeId) {
        this.user = user;
        this.text = text;
        this.placeId = placeId;

    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
