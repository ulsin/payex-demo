package com.tvapi.payex.models;

public class Show {
    private String name;
    private int showId;

    public Show() {
    }

    public Show(String name, int showId) {
        this.name = name;
        this.showId = showId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }
}
