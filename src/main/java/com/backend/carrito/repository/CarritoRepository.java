package com.backend.carrito.repository;

import com.backend.carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    // Permite buscar si el usuario ya tiene un carrito en curso
    Optional<Carrito> findByUsuarioIdAndEstado(String usuarioId, String estado);
}