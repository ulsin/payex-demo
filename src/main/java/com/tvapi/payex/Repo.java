package com.tvapi.payex;

import com.tvapi.payex.models.Show;
import com.tvapi.payex.models.Test;
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
import java.util.List;
import java.util.Scanner;

@Repository
public class Repo {

    private final Logger logger = LoggerFactory.getLogger(Repo.class);
    @Autowired
    private JdbcTemplate db;

    public Repo() {
        System.out.println("Repo constructor ran");

        try {
            getShowFromAPIByName("Girls");

        } catch (Exception e) {
            System.out.println("getShowFromAPIByName crasshed");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        populateDbFromFile();
    }

    public void populateDbFromFile() {
        List<String> showNames = getShowNamesFromFile();
        System.out.println(showNames);

        for (String showName : showNames) {
            insertShow(showName, -1);
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

    private void getShowIdFromApi() {
        List<Show> shows = getAllShows();
    }

    public void insertShow(String name, int showId) {
        try {
            db.update("insert into Show(name, showId) values(?, ?)", name, showId);
        } catch (Exception e) {
            System.out.println("Error in Repo.insertShow()");
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

    public void updateShowIdByName(String name, int showId) {
        try {
            db.update("update Show set showId=? where name=?", showId, name);
        } catch (Exception e) {
            System.out.println("Error in Repo.updateShowByName()");
            logger.error(String.valueOf(e));
        }
    }

    // TODO move to controller, deals with network stuff
    public void getShowFromAPIByName(String name) throws IOException {
        URL url = new URL("https://api.tvmaze.com/singlesearch/shows?q=" + name);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setDoOutput(true);

        int status = connection.getResponseCode();

        if (status == 200) {
            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }

            scanner.close();

            System.out.println(stringBuilder);
        } else {
            System.out.println("Status was " + status);
        }

        connection.disconnect();
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
