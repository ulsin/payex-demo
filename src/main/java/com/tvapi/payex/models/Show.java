package com.tvapi.payex.models;

public class Show {
    private String name;
    private int showId;

    private double rating;

    private String network;

    public Show() {
    }

    public Show(String name, int showId, double rating, String network) {
        this.name = name;
        this.showId = showId;
        this.rating = rating;
        this.network = network;
    }

    @Override
    public String toString() {
        return "Show{" +
                "name='" + name + '\'' +
                ", showId=" + showId +
                ", rating=" + rating +
                ", network=" + network +
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

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
