package com.tvapi.payex.models;

import java.util.List;

public class ShowWithEpisodes {
    private Show show;
    private List<Episode> episodes;

    public ShowWithEpisodes(Show show, List<Episode> episodes) {
        this.show = show;
        this.episodes = episodes;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
