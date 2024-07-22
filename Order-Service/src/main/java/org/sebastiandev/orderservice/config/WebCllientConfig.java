package org.sebastiandev.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebCllientConfig {

    @Bean
    @LoadBalanced // LoadBalanced annotation is used to enable client-side load balancing
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }
}
