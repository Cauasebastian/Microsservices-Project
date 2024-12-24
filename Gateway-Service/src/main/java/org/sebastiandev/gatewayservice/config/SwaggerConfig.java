package org.sebastiandev.gatewayservice.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi productServiceApi() {
        return GroupedOpenApi.builder()
                .group("Product Service")
                .pathsToMatch("/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderServiceApi() {
        return GroupedOpenApi.builder()
                .group("Order Service")
                .pathsToMatch("/orders/**")
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryServiceApi() {
        return GroupedOpenApi.builder()
                .group("Inventory Service")
                .pathsToMatch("/inventory/**")
                .build();
    }
}
