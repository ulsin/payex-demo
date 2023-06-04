package com.tvapi.payex;

import com.tvapi.payex.models.Test;
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

    @Autowired
    private JdbcTemplate db;

    public Repo() {
        System.out.println("Repo constructor ran");


    }

    @EventListener(ApplicationReadyEvent.class)
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

    public void insertShow(String name, int showId) {
        try {
            db.update("insert into Show(name, showId) values(?, ?)", name, showId);
        } catch (Exception e) {
            System.out.println("Error in Repo.insertShow()");
            System.out.println(e);
        }
    }

    // TODO move to controller, deals with network stuff
    public void getFromAPI() throws IOException {
        URL url = new URL("https://api.tvmaze.com/singlesearch/shows?q=girls");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
    }

    public void RepoTest() {
        System.out.println("repo test function ran");

        try {
            List<Test> testList = db.query("select * from Test", new BeanPropertyRowMapper<>(Test.class));
            System.out.println(testList);
        } catch (Exception e) {
            System.out.println("error in repotest");
            System.out.println(e);
        }
    }
}
