package com.backend.carrito.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ProductoClient {

    private final RestClient restClient;
    private final String msProductoUrl;

    public ProductoClient(
            RestClient.Builder restClientBuilder,
            @Value("${producto.api.url}") String msProductoUrl) {
        
        // JdkClientHttpRequestFactory usa el HttpClient nativo de Java 11+ que sí soporta PATCH
        this.restClient = restClientBuilder
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
        this.msProductoUrl = msProductoUrl;
    }

    public void actualizarStock(Long productoId, Integer cantidad, String token) {
        String url = msProductoUrl + "/" + productoId + "/stock";

        // Asegura el prefijo 'Bearer ' requerido por Microsoft / Spring Security
        String authorizationHeader = (token != null && token.startsWith("Bearer ")) 
                ? token 
                : "Bearer " + token;

        restClient.patch()
                .uri(url)
                .header("Authorization", authorizationHeader)
                .body(Map.of("cantidad", cantidad))
                .retrieve()
                .toBodilessEntity();
    }
}