package com.backend.carrito.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductoClient {

    // Inyectamos la URL desde application.properties
    @Value("${producto.api.url}")
    private String productoApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void actualizarStock(Long productoId, Integer cantidadVariacion, String token) {
        HttpHeaders headers = new HttpHeaders();
        // Propagamos el token del usuario hacia ms-producto vía el API Gateway
        headers.set("Authorization", "Bearer " + token); 
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        // Construimos la URL apuntando al API Gateway
        String url = productoApiUrl + "/" + productoId + "/stock?cantidadVariacion=" + cantidadVariacion;

        // Hacemos la petición HTTP
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al comunicarse con ms-producto en AWS para actualizar stock");
        }
    }
}