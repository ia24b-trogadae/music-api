package ch.example.musicapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Music API",
                version = "1.0",
                description = "Backend API for managing albums and songs",
                contact = @Contact(
                        name = "Elena"
                ),
                license = @License(
                        name = "School Project"
                )
        )
)
public class OpenApiConfig {
}