package com.example.roadmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Entry point for the Spring Boot application.
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class RoadMap2026Application {

  public static void main(String[] args) {
    SpringApplication.run(RoadMap2026Application.class, args);
  }

}
