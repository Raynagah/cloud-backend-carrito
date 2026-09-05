package com.backend.carrito.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del usuario autenticado (extraído del token de Azure AD)
    @Column(nullable = false, name = "usuario_id")
    private String usuarioId;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private String estado; // Ejemplo: ACTIVO, COMPLETADO, CANCELADO

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.total == null) this.total = BigDecimal.ZERO;
        if (this.estado == null) this.estado = "ACTIVO";
    }
}