package com.backend.carrito.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarritoDTO(
        Long id,
        String usuarioId,
        LocalDateTime fechaCreacion,
        BigDecimal total,
        String estado,
        List<ItemCarritoDTO> items
) {}