package com.snapshot.lumina.businesslogic.presentation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI luminaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lumina Business Logic API")
                        .description("REST API for Lumina workspace management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Lumina Team")
                                .email("support@lumina.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.lumina.com")
                                .description("Production Server")
                ));
    }
}