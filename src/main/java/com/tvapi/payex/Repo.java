package com.tvapi.payex;

import org.springframework.stereotype.Repository;

@Repository
public class Repo {
    public Repo() {
        System.out.println("Repo constructor ran");
    }

    public void Test() {
        System.out.println("repo test function ran");
    }
}
