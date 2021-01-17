package org.example.cab_management_portal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MainApplication {

    public static void main(String[] args) {
        try {
            SpringApplication application = new SpringApplication(MainApplication.class);
            application.run(args);
        }
        catch (Exception ex) {
            log.error("error: {}",ex);
        }
    }
}
