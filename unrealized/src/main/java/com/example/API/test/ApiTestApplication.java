package com.example.API.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class ApiTestApplication {

    public static void main(String[] args) {

        SpringApplication.run(ApiTestApplication.class, args);

    }

}
