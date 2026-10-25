package com.hotel.huespedes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI huespedesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Huéspedes Service API - Sistema de Alquileres de Hotel")
                        .description("Microservicio encargado de registrar, consultar, validar y gestionar el ciclo de vida de los huéspedes (RF04, RF05, RF06, RN04).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mafer"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io/")));
    }
}
