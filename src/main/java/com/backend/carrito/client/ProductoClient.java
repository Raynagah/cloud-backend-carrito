package com.backend.carrito.client;

import org.springframework.beans.factory.annotation.Value;
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
        
        this.restClient = restClientBuilder.build();
        this.msProductoUrl = msProductoUrl;
    }

    public void actualizarStock(Long productoId, Integer cantidad, String token) {
        String url = msProductoUrl + "/api/v1/productos/" + productoId + "/stock";

        // Usamos RestClient que soporta PATCH de forma nativa
        restClient.patch()
                .uri(url)
                .header("Authorization", token) // El token ya debería venir con "Bearer "
                .body(Map.of("cantidad", cantidad))
                .retrieve()
                .toBodilessEntity(); 
    }
}