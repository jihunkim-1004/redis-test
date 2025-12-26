package com.example.redistest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("로컬 개발 서버");

        Server prodServer = new Server();
        prodServer.setUrl("/");
        prodServer.setDescription("운영 서버");

        Contact contact = new Contact();
        contact.setName("Redis Test Team");
        contact.setEmail("contact@example.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Spring Redis 자료구조 API")
                .version("1.0.0")
                .contact(contact)
                .description("Spring Data Redis의 다양한 자료구조(String, List, Set, Sorted Set, Hash, HyperLogLog, Geo)를 활용하는 REST API 예제입니다.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(prodServer, localServer));
    }
}


