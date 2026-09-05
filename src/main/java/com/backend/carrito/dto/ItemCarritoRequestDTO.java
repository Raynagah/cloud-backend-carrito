package com.backend.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemCarritoRequestDTO(
        @NotNull(message = "El ID del producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        Integer cantidad,
        
        // En un entorno 100% real, el precio se consultaría al ms-productos, 
        // pero lo recibiremos aquí para mantener la independencia del servicio.
        @NotNull(message = "El precio unitario es obligatorio")
        @Min(value = 0, message = "El precio no puede ser negativo")
        java.math.BigDecimal precioUnitario 
) {}