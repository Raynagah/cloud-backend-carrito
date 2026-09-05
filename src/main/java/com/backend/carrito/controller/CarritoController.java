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
        
        // Extrae el identificador único del usuario desde los claims del token de Azure AD
        String usuarioId = jwt.getSubject(); 
        
        CarritoDTO carrito = carritoService.agregarItem(usuarioId, dto);
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
        
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}