package com.bajrix.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("BajriX Marketplace API")
                        .version("1.0")
                        .description("BajriX construction marketplace API"))
                .components(new Components()
                        .addSecuritySchemes(
                                "adminToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Admin-Token")
                                        .description("Admin token obtained from POST /api/admin/login")
                        )
                );
    }
}