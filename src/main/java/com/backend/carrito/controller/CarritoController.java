package com.backend.carrito.controller;

import com.backend.carrito.dto.CarritoDTO;
import com.backend.carrito.dto.ItemCarritoRequestDTO;
import com.backend.carrito.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    // Método auxiliar para no repetir la lógica de extracción del nombre
    private String obtenerNombreUsuario(Jwt jwt) {
        String nombre = jwt.getClaimAsString("name");
        return (nombre != null && !nombre.isBlank()) ? nombre : "Usuario Desconocido";
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ItemCarritoRequestDTO dto) {

        String usuarioId = jwt.getSubject();
        String usuarioNombre = obtenerNombreUsuario(jwt);
        String tokenStr = jwt.getTokenValue();

        // Agregamos usuarioNombre a la llamada del Service
        CarritoDTO carrito = carritoService.agregarItem(usuarioId, usuarioNombre, dto, tokenStr);
        return ResponseEntity.ok(carrito);
    }

    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarritoActivo(@AuthenticationPrincipal Jwt jwt) {
        String usuarioId = jwt.getSubject();

        // Este método en el Service no requiere el nombre, se mantiene igual
        CarritoDTO carrito = carritoService.obtenerCarritoActivo(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@AuthenticationPrincipal Jwt jwt) {
        String usuarioId = jwt.getSubject();
        String usuarioNombre = obtenerNombreUsuario(jwt);
        String tokenStr = jwt.getTokenValue(); // <-- Extraemos el token string

        // Agregamos usuarioNombre a la llamada del Service
        carritoService.vaciarCarrito(usuarioId, usuarioNombre, tokenStr);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para eliminar un solo ítem del carrito
    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productoId) {

        String usuarioId = jwt.getSubject();
        String usuarioNombre = obtenerNombreUsuario(jwt);
        String tokenStr = jwt.getTokenValue();

        // Agregamos usuarioNombre a la llamada del Service
        CarritoDTO carrito = carritoService.eliminarItem(usuarioId, usuarioNombre, productoId, tokenStr);
        return ResponseEntity.ok(carrito);
    }
}