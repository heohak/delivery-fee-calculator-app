package org.example.fujitsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FujitsuApplication {

    public static void main(String[] args) {
        SpringApplication.run(FujitsuApplication.class, args);
    }

}
