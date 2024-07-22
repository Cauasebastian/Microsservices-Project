package org.sebastiandev.gatewayservice.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.client.HttpClient;

@org.springframework.context.annotation.Configuration
public class gatewayconfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
    }
}
