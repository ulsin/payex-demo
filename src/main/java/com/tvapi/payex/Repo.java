package com.tvapi.payex;

import com.tvapi.payex.models.*;
import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

@Repository
public class Repo {

    private final Logger logger = LoggerFactory.getLogger(Repo.class);
    @Autowired
    private JdbcTemplate db;

    public Repo() {
        System.out.println("Repo constructor ran");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        populateDbFromFile();
        List<Show> shows = getAllShows();

        populateDbFromApi("Wynonna Earp");


//        for (Show show : shows) {
//            populateDbFromApi(show.getName());
//        }

        logger.info("All shows are now populated from the api, and info rests in database");

        // TODO generate report


    }

    public void reportTop10Shows() {
//        db.query();
    }

    public void reportTopNetwork() {

    }

    public void populateDbFromApi(String name) {
        try {
            ShowWithEpisodes showWithEpisodes = getShowWithEpisodesFromAPIByName(name);

            Show show = showWithEpisodes.getShow();

            List<Episode> episodes = showWithEpisodes.getEpisodes();

            updateShowIdByName(show);

            for (Episode episode : episodes) {
                insertEpisode(episode);
            }


        } catch (Exception e) {
            System.out.println("getShowFromAPIByName crashed");
            logger.error(String.valueOf(e));
        }
    }

    public void populateDbFromFile() {
        List<String> showNames = getShowNamesFromFile();
        System.out.println(showNames);

        for (String showName : showNames) {
            insertShow(new Show(showName, -1, -1.0, -1));
        }
    }

    public List<String> getShowNamesFromFile() {
        List<String> showNames = new ArrayList<>();

        try {
            File file = new File("Vedlegg.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                showNames.add(scanner.nextLine());
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error in readFile()");
        }

        return showNames;
    }

    public void insertShow(Show show) {
        try {
            db.update("insert into Show(name, showId, rating, networkId) values(?, ?, ?, ?)",
                    show.getName(), show.getShowId(), show.getRating(), show.getNetworkId());
        } catch (Exception e) {
            System.out.println("Error in Repo.insertShow()");
            logger.error(String.valueOf(e));
        }
    }

    public void insertEpisode(Episode episode) {
        try {
            db.update("insert into Episode(showId, name, season, episode, rating) values (?, ?, ?, ?, ?)",
                    episode.getShowId(), episode.getName(), episode.getSeason(), episode.getEpisode(), episode.getRating());
        } catch (Exception e) {
            System.out.println("Error when inserting episode into db");
            logger.error(String.valueOf(e));
        }
    }

    public List<Show> getAllShows() {
        try {
            return db.query("select * from Show", new BeanPropertyRowMapper<>(Show.class));
        } catch (Exception e) {
            System.out.println("Error in getAllShows");
            logger.error(String.valueOf(e));
            return null;
        }
    }

    public void updateShowIdByName(Show show) {
        try {
            db.update("update Show set showId=?, rating=?, networkId=? where name=?", show.getShowId(), show.getRating(), show.getNetworkId(), show.getName());
        } catch (Exception e) {
            System.out.println("Error in Repo.updateShowByName()");
            logger.error(String.valueOf(e));
        }
    }

    // Saw they had endpoint for getting show and episodes, a bit more lazy than doing it one per,
    // but doing it like this to better respect how many queries they want
    public ShowWithEpisodes getShowWithEpisodesFromAPIByName(String name) throws IOException {
        int showId = -1;
        double showRating = -1.0;
        int networkId = -1;
        List<Episode> episodes = new ArrayList<>();


        URL url = new URL("https://api.tvmaze.com/singlesearch/shows?q=" + name + "&embed=episodes");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();

        if (status == 429) {
            System.out.println("Status is 429, sleeping for 20s to respect the API restrictions");

            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

        } else if (status != 200) {
            System.out.println("Status was " + status);
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }

            scanner.close();

            JSONObject json = new JSONObject(new JSONTokener(stringBuilder.toString()));

            showId = json.getInt("id");
            showRating = json.getJSONObject("rating").getDouble("average");

            JSONObject networkJson = json.getJSONObject("network");
            networkId = networkJson.getInt("id");
            String networkName = networkJson.getString("name");
            String networkCountry = networkJson.getJSONObject("country").getString("name");

            System.out.println("show rating + network id" + showRating + " " + networkId);

            // TODO make sure this goes somewhere
            Network network = new Network(networkName, networkId, networkCountry);


            JSONArray episodesArray = json.getJSONObject("_embedded").getJSONArray("episodes");

            for (int i = 0; i < episodesArray.length(); i++) {
                JSONObject episodeJson = episodesArray.getJSONObject(i);
                String episodeName = episodeJson.getString("name");
                int season = episodeJson.getInt("season");
                int episode = episodeJson.getInt("number");
                double episodeRating = episodeJson.getJSONObject("rating").getDouble("average");
                episodes.add(new Episode(showId, episodeName, season, episode, episodeRating, networkId));
            }
        }

        connection.disconnect();

        return new ShowWithEpisodes(new Show(name, showId, showRating, networkId), episodes);
    }

    public void RepoTest() {
        System.out.println("repo test function ran");

        try {
            List<Test> testList = db.query("select * from Test", new BeanPropertyRowMapper<>(Test.class));
            System.out.println(testList);
        } catch (Exception e) {
            System.out.println("error in Repo.RepoTest");
            logger.error(String.valueOf(e));
        }
    }
}
