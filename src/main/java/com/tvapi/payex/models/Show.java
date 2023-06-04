package com.tvapi.payex.models;

public class Show {
    private String name;
    private int showId;

    private double rating;

    private int networkId;

    public Show() {
    }

    public Show(String name, int showId, double rating, int networkId) {
        this.name = name;
        this.showId = showId;
        this.rating = rating;
        this.networkId = networkId;
    }

    @Override
    public String toString() {
        return "Show{" +
                "name='" + name + '\'' +
                ", showId=" + showId +
                ", rating=" + rating +
                ", networkId=" + networkId +
                '}';
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }
}
