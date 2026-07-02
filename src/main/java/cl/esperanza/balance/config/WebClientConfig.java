package cl.esperanza.balance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient telemetriaWebClient(@Value("${telemetria.service.url:http://localhost:8086/api/v1/telemetria}") String telemetriaServiceUrl){
        return WebClient.builder().baseUrl(telemetriaServiceUrl).build();
    }

    @Bean
    public WebClient facturacionWebClient(@Value("${facturacion.service.url:http://localhost:8083/api/v1/facturacion}") String facturacionServiceUrl){
        return WebClient.builder().baseUrl(facturacionServiceUrl).build();
    }
    
    @Bean
    public WebClient incidenciasWebClient(@Value("${incidencias.service.url:http://localhost:8082/api/v1/incidencias}") String incidenciasServiceUrl){
        return WebClient.builder().baseUrl(incidenciasServiceUrl).build();
    }
}
