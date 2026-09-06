package com.backend.carrito.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductoClient {

    private final RestClient restClient;
    private final String msProductoUrl;

    public ProductoClient(
            RestClient.Builder restClientBuilder,
            @Value("${producto.api.url}") String msProductoUrl) {
        
        this.restClient = restClientBuilder
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
        this.msProductoUrl = msProductoUrl;
    }

    public void actualizarStock(Long productoId, Integer cantidad, String token) {
        // Se concatena cantidadVariacion como Query Parameter en lugar de usar RequestBody
        String url = msProductoUrl + "/" + productoId + "/stock?cantidadVariacion=" + cantidad;

        String authorizationHeader = (token != null && token.startsWith("Bearer ")) 
                ? token 
                : "Bearer " + token;

        restClient.patch()
                .uri(url)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .toBodilessEntity();
    }
}