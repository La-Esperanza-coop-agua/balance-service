package cl.esperanza.balance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient telemetriaWebClient(WebClient.Builder builder){
        return builder.baseUrl("http://localhost:8086/api/v1/telemetria").build();
    }

    @Bean
    public WebClient facturacionWebClient(WebClient.Builder builder){
        return builder.baseUrl("http://localhost:8083/api/v1/facturacion").build();
    }
}
