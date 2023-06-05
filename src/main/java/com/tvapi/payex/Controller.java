package com.tvapi.payex;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class Controller {

    @Autowired
    Repo repo;

    @GetMapping("/report/shows")
    public String getAllShows(HttpServletResponse response) throws IOException {
        return repo.reportAllShows();
    }

    @GetMapping("/report/topNetworks")
    public String getTopNetworks(HttpServletResponse response) throws IOException {
        return repo.reportTopNetworks();
    }

    @GetMapping("/report/topShows")
    public String getTopShows(HttpServletResponse response) throws IOException {
        return repo.reportTop10Shows();
    }

    @GetMapping("/report/all")
    public String getAllReports(HttpServletResponse response) throws IOException {
        String spacer = "\n\n\n";
        return repo.reportTopNetworks() + spacer + repo.reportTop10Shows() + spacer + repo.reportAllShows();
    }
}
