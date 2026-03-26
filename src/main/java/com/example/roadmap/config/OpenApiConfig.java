package com.example.roadmap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI roadMapOpenApi() {
    return new OpenAPI().info(new Info()
        .title("RoadMap2026 API")
        .description("REST API for managing users, roadmaps, roadmap items, tags and comments.")
        .version("1.0.0")
        .contact(new Contact()
            .name("Vladislav Mogilny")
            .email("vladislav@example.com"))
        .license(new License()
            .name("Educational use")));
  }
}
