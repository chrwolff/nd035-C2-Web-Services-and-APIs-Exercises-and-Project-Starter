package com.udacity.vehicles;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI metaData() {
        return new OpenAPI()
            .info(new Info().title("Udacity Cars")
                .description("Awesome ideas are driven by you")
                .version("1.0.0")
                .license(new License().name("Apache License Version 2.0").url("https://www.apache.org/licenses/LICENSE-2.0\""))
                .contact(new Contact().name("Jane Doe")));
    }
}
