package com.backend.carrito.repository;

import com.backend.carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    // Permite buscar el carrito en curso de un usuario por su ID/correo y estado
    Optional<Carrito> findByUsuarioIdAndEstado(String usuarioId, String estado);

    // Permite buscar cualquier carrito por usuarioId
    Optional<Carrito> findByUsuarioId(String usuarioId);

    // Permite eliminar directamente por usuarioId
    void deleteByUsuarioId(String usuarioId);
}
