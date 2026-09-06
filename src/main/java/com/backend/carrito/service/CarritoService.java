package com.backend.carrito.service;

import com.backend.carrito.client.ProductoClient;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoClient productoClient; 

    @Transactional
    public CarritoDTO agregarItem(String usuarioId, ItemCarritoRequestDTO dto, String token) {
        // 1. Restar stock en MS-Productos a través de AWS API Gateway
        // Enviamos cantidad negativa para descontar del stock
        productoClient.actualizarStock(dto.productoId(), -dto.cantidad(), token);

        // 2. Lógica normal del carrito
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

    @Transactional
    public CarritoDTO obtenerCarritoActivo(String usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return convertirADTO(carrito);
    }

    @Transactional
    public void vaciarCarrito(String usuarioId, String token) {
        carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO").ifPresent(carrito -> {
            
            // 1. Devolver el stock de todos los items al MS-Productos
            for (ItemCarrito item : carrito.getItems()) {
                productoClient.actualizarStock(item.getProductoId(), item.getCantidad(), token);
            }

            // 2. Vaciar el carrito en BD
            carrito.getItems().clear();
            carrito.setTotal(BigDecimal.ZERO);
            carritoRepository.save(carrito);
        });
    }

    // NUEVO MÉTODO: Eliminar un solo ítem y devolver su stock
    @Transactional
    public CarritoDTO eliminarItem(String usuarioId, Long productoId, String token) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        
        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            
            // 1. Devolver la cantidad al stock de MS-Productos
            productoClient.actualizarStock(productoId, item.getCantidad(), token);
            
            // 2. Quitar del carrito
            carrito.getItems().remove(item);
            
            recalcularTotal(carrito);
            return convertirADTO(carritoRepository.save(carrito));
        }
        
        return convertirADTO(carrito);
    }

    // =========================================================================
    // MÉTODOS PRIVADOS AUXILIARES (Mantienen igual que tu versión)
    // =========================================================================

    private Carrito obtenerOCrearCarrito(String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO")
                .orElseGet(() -> {
                    Carrito nuevoCarrito = Carrito.builder()
                            .usuarioId(usuarioId)
                            .estado("ACTIVO")
                            .total(BigDecimal.ZERO)
                            .fechaCreacion(LocalDateTime.now())
                            .items(new ArrayList<>()) // <-- Asigna explícitamente una lista vacía
                            .build();
                    return carritoRepository.save(nuevoCarrito);
                });

        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        return carrito;
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