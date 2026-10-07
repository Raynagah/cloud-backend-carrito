package com.backend.carrito.dto; // O com.backend.carrito.dto según el microservicio

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCreadaEvent implements Serializable {
    @JsonProperty ("orden_id")
    private Long ordenId;

    private String usuarioId;       // Azure Subject/OID (ej: QUuWl6E3O...) -> Usado para BD
    private String usuarioNombre;   // Nombre (ej: "Juan Pérez") -> Usado para logs/UI
    private String usuarioCorreo;   // Correo (ej: "juan@correo.com") -> Usado para logs/notificaciones
    private LocalDateTime fechaCreacion;
    private String estado;
    private BigDecimal total;
}