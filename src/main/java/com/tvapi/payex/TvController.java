package com.tvapi.payex;





import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class TvController {

    @Autowired
    Repo repo;

    @GetMapping("/test")
    public String getTest(HttpServletResponse response) throws IOException {
        System.out.println("Controller Get Test was run");
        repo.Test();

        return "test from rest";
    }
}
