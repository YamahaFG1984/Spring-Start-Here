package com.example.ssh_ch11_ex2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
