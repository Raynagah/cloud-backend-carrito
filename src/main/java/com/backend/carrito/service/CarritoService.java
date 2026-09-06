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
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

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

    // CAMBIO AQUI: Si no existe, creamos uno vacío en lugar de lanzar error
    @Transactional
    public CarritoDTO obtenerCarritoActivo(String usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return convertirADTO(carrito);
    }

    // CAMBIO AQUI: Usamos ifPresent para no fallar si intentan vaciar un carrito inexistente
    @Transactional
    public void vaciarCarrito(String usuarioId) {
        carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO").ifPresent(carrito -> {
            carrito.getItems().clear();
            carrito.setTotal(BigDecimal.ZERO);
            carritoRepository.save(carrito);
        });
    }

    // =========================================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // =========================================================================

    // Metodo extraído para no repetir código entre agregar y obtener
    private Carrito obtenerOCrearCarrito(String usuarioId) {
        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO")
                .orElseGet(() -> carritoRepository.save(
                        Carrito.builder()
                                .usuarioId(usuarioId)
                                .estado("ACTIVO")
                                .total(BigDecimal.ZERO)
                                .build()
                ));
    }

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
                        // Calculamos el subtotal en tiempo real para el DTO
                        item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())) 
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