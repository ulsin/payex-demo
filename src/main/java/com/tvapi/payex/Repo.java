package com.tvapi.payex;

import com.tvapi.payex.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

@Repository
public class Repo {

    private final Logger logger = LoggerFactory.getLogger(Repo.class);
    @Autowired
    private JdbcTemplate db;

    public Repo() {
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        populateDbFromFile();
        List<Show> shows = getAllShows();

//        populateDbFromApi("Wynonna Earp");

        for (Show show : shows) {
            populateDbFromApi(show.getName());
        }

        logger.info("All shows are now populated from the api, and info rests in database");

        deleteFiles();

        String top10 = reportTop10Shows();
        writeReport("reportTop10.txt", top10);

        String topNetwork = reportTopNetworks();
        writeReport("reportTopNetwork.txt", topNetwork);

        String allShows = reportAllShows();
        writeReport("reportAllShows.txt", allShows);

        logger.info("Report is written");
    }

    public void deleteFiles() {
        new File("reportTop10.txt").delete();
        new File("reportTopNetwork.txt").delete();
        new File("reportAllShows.txt").delete();
    }

    public void writeReport(String fileName, String fileContent) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(fileContent);
            writer.close();
        } catch (Exception e) {
            System.out.println("Got exeption when writing to file: " + fileName);
        }

    }

    public String reportAllReports() {
        String spacer = "\n\n\n";
        return reportTopNetworks() + spacer + reportTop10Shows() + spacer + reportAllShows();
    }

    // * Summary - Skal liste alle registrerte tv-serier
    public String reportAllShows() {
        List<Show> shows = getAllShows();
        List<Episode> episodes = getAllEpisodes();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SHOW_NAME;NETWORK;EPISODE_COUNT;\n");

        for (Show show : shows) {
            int episodeCount = episodes.stream().filter(f -> show.getShowId() == f.getShowId()).collect(Collectors.toList()).size();
            stringBuilder.append(show.getName() + ";" + show.getNetwork() + ";" + episodeCount + "\n");
        }

        return stringBuilder.toString();
    }

    // * Top 10 - Skal liste serier sortert på rating
    public String reportTop10Shows() {
        try {
            List<Show> top10Shows = db.query("select * from Show order by rating desc limit 10", new BeanPropertyRowMapper<>(Show.class));

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("name;rating;showId;networkId\n");

            for (Show show : top10Shows) {
                stringBuilder.append(show.getName() + ';' + show.getRating() + ';' + show.getShowId() + ';' + show.getNetwork() + '\n');
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            System.out.println("Error when getting top 10 report");
            logger.error(String.valueOf(e));
            return null;
        }
    }

    public String reportTopNetworks() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("AVERAGE_RATING;NETWORK;TOP_RATED_SHOW;TOP_RATING;SHOW_COUNT\n");

        List<String> lines = new ArrayList<>();

        List<Show> shows = getAllShows();

        Map<String, List<Show>> showByNetwork = new HashMap<>();


        // Grouping show by network
        for (Show show : shows) {
            List<Show> mapShow = showByNetwork.get(show.getNetwork());
            if (mapShow == null) {
                mapShow = new ArrayList<>();
                // might need to put this outside of if, in case pointers acts wierd
                showByNetwork.put(show.getNetwork(), mapShow);
            }
            mapShow.add(show);
        }

        // making the entires per network group
        for (String network : showByNetwork.keySet()) {
            List<Show> mapShow = showByNetwork.get(network);

            double sum = 0.0;
            Show topShow = mapShow.get(0);
            for (Show show : mapShow) {
                sum += show.getRating();
                if (show.getRating() > topShow.getRating()) {
                    topShow = show;
                }
            }
            lines.add((sum / mapShow.size()) + ";" + network + ";" + topShow.getName() + ";" + topShow.getRating() + ";" + mapShow.size() + "\n");
        }

        lines.sort(Comparator.naturalOrder());

        for (int i = lines.size() - 1; i >= lines.size() - 10; i--) {
            try {
                stringBuilder.append(lines.get(i));
            } catch (Exception e) {
                break;
            }
        }

        return stringBuilder.toString();
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

        for (String showName : showNames) {
            insertShow(new Show(showName, -1, -1.0, "-1"));
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
            db.update("insert into Show(name, showId, rating, network) values(?, ?, ?, ?)",
                    show.getName(), show.getShowId(), show.getRating(), show.getNetwork());
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

    public List<Episode> getAllEpisodes() {
        try {
            return db.query("select * from Episode", new BeanPropertyRowMapper<>(Episode.class));
        } catch (Exception e) {
            System.out.println("Error in getAllEpisodes");
            logger.error(String.valueOf(e));
            return null;
        }
    }

    public void updateShowIdByName(Show show) {
        try {
            db.update("update Show set showId=?, rating=?, network=? where name=?", show.getShowId(), show.getRating(), show.getNetwork(), show.getName());
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
        String networkName = "N/A";
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
            try {
                showRating = json.getJSONObject("rating").getDouble("average");
            } catch (Exception e) {
                System.out.println("Error in getting showRating");
                showRating = -1.0;
            }

            try {
                JSONObject networkJson = json.getJSONObject("network");
                networkId = networkJson.getInt("id");
                networkName = networkJson.getString("name");
                String networkCountry = networkJson.getJSONObject("country").getString("name");

                // TODO make sure this goes somewhere
                Network network = new Network(networkName, networkId, networkCountry);
            } catch (Exception e) {
                System.out.println("Error when parsing network");
                networkId = -1;
            }


            JSONArray episodesArray = json.getJSONObject("_embedded").getJSONArray("episodes");

            for (int i = 0; i < episodesArray.length(); i++) {
                JSONObject episodeJson = episodesArray.getJSONObject(i);
                String episodeName = episodeJson.getString("name");
                int season = episodeJson.getInt("season");
                int episode = episodeJson.getInt("number");
                double episodeRating;
                try {
//                    episodeRating = episodeJson.getDouble("rating");
                    JSONObject episodeRatingObj = episodeJson.getJSONObject("rating");
                    episodeRating = episodeRatingObj.getDouble("average");

                } catch (Exception e) {
                    episodeRating = -1.0;
                }
                episodes.add(new Episode(showId, episodeName, season, episode, episodeRating));
            }
        }

        connection.disconnect();

        return new ShowWithEpisodes(new Show(name, showId, showRating, networkName), episodes);
    }
}
