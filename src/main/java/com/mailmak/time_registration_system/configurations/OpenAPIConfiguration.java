package com.mailmak.time_registration_system.configurations;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Session Logger")
                        .version("1.0")
                        .description("Session Logger API documentation")
                        .contact(new Contact()
                                .name("cs-24-sw-03-12")
                                .email("cs-24-sw-03-12@student.aau.com")));
    }
}
