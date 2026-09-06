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
@RequestMapping("/api/v1/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ItemCarritoRequestDTO dto) {
        
        String usuarioId = jwt.getSubject(); 
        String tokenStr = jwt.getTokenValue(); // <-- Extraemos el token string para MS-Producto
        
        CarritoDTO carrito = carritoService.agregarItem(usuarioId, dto, tokenStr);
        return ResponseEntity.ok(carrito);
    }

    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarritoActivo(@AuthenticationPrincipal Jwt jwt) {
        String usuarioId = jwt.getSubject();
        
        CarritoDTO carrito = carritoService.obtenerCarritoActivo(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@AuthenticationPrincipal Jwt jwt) {
        String usuarioId = jwt.getSubject();
        String tokenStr = jwt.getTokenValue(); // <-- Extraemos el token string
        
        carritoService.vaciarCarrito(usuarioId, tokenStr);
        return ResponseEntity.noContent().build();
    }

    // NUEVO: Endpoint para eliminar un solo ítem del carrito
    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productoId) {
        
        String usuarioId = jwt.getSubject();
        String tokenStr = jwt.getTokenValue();
        
        CarritoDTO carrito = carritoService.eliminarItem(usuarioId, productoId, tokenStr);
        return ResponseEntity.ok(carrito);
    }
}