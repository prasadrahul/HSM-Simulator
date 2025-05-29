package com.hsm.simulator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HSM Simulator API")
                        .version("1.0")
                        .description("HSM Simulator API documentation")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("API Support")
                                .email("prasad_rahul@yahoo.co.in")
                                .url("https://www.rahulnotebook.com/")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("http://localhost:8080")
                        .description("HSM Server"));
    }
}


