package com.backend.carrito.service;

import com.backend.carrito.dto.CarritoDTO;
import com.backend.carrito.dto.ItemCarritoDTO;
import com.backend.carrito.dto.ItemCarritoRequestDTO;
import com.backend.carrito.model.Carrito;
import com.backend.carrito.model.ItemCarrito;
import com.backend.carrito.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;

    @Transactional
    public CarritoDTO agregarItem(String usuarioId, ItemCarritoRequestDTO dto) {
        // Busca un carrito activo del usuario, si no existe, crea uno nuevo
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO")
                .orElseGet(() -> carritoRepository.save(
                        Carrito.builder()
                                .usuarioId(usuarioId)
                                .estado("ACTIVO")
                                .total(BigDecimal.ZERO)
                                .build()
                ));

        // Verifica si el producto ya está en el carrito para sumar la cantidad
        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(dto.productoId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + dto.cantidad());
        } else {
            ItemCarrito nuevoItem = ItemCarrito.builder()
                    .carrito(carrito)
                    .productoId(dto.productoId())
                    .cantidad(dto.cantidad())
                    .precioUnitario(dto.precioUnitario())
                    .build();
            carrito.getItems().add(nuevoItem);
        }

        recalcularTotal(carrito);
        return convertirADTO(carritoRepository.save(carrito));
    }

    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarritoActivo(String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO")
                .orElseThrow(() -> new RuntimeException("No se encontró un carrito activo para el usuario."));
        return convertirADTO(carrito);
    }

    @Transactional
    public void vaciarCarrito(String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO")
                .orElseThrow(() -> new RuntimeException("No se encontró un carrito activo."));
        
        carrito.getItems().clear();
        carrito.setTotal(BigDecimal.ZERO);
        carritoRepository.save(carrito);
    }

    // =========================================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // =========================================================================

    private void recalcularTotal(Carrito carrito) {
        BigDecimal total = carrito.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrito.setTotal(total);
    }

    private CarritoDTO convertirADTO(Carrito carrito) {
        var itemsDTO = carrito.getItems().stream()
                .map(item -> new ItemCarritoDTO(
                        item.getId(),
                        item.getProductoId(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                )).toList();

        return new CarritoDTO(
                carrito.getId(),
                carrito.getUsuarioId(),
                carrito.getFechaCreacion(),
                carrito.getTotal(),
                carrito.getEstado(),
                itemsDTO
        );
    }
}