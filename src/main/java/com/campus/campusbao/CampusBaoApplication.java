package com.campus.campusbao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.campus.campusbao")
public class CampusBaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusBaoApplication.class, args);
    }
}