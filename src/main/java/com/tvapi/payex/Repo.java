package com.tvapi.payex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Repo {

    @Autowired
    private JdbcTemplate db;

    public Repo() {
        System.out.println("Repo constructor ran");
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
