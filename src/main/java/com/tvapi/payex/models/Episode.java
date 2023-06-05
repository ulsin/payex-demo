package com.tvapi.payex.models;

public class Episode {
    private int showId;
    private String name;
    private int season;
    private int episode;

    private double rating;

    public Episode() {
    }

    public Episode(int showId, String name, int season, int episode, double rating) {
        this.showId = showId;
        this.name = name;
        this.season = season;
        this.episode = episode;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "showId=" + showId +
                ", name='" + name + '\'' +
                ", season=" + season +
                ", episode=" + episode +
                ", rating=" + rating +
                '}';
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
