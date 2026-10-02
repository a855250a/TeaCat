package com.teacat.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
        .info(
                new Info()
                        .title("TeaCat API")
                        .version("1.0")
                        .description("Pet Management System API")
        );
    }
}
