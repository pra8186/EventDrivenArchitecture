package com.eventdriven.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CoreServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(CoreServiceApp.class, args);
    }
}
