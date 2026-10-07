package com.backend.carrito.listener;

import com.backend.carrito.config.RabbitMQConfig;
import com.backend.carrito.dto.OrdenCreadaEvent;
import com.backend.carrito.service.CarritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarritoEventListener {

    private final CarritoService carritoService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CARRITO_LIMPIAR)
    public void handleOrdenCreada(OrdenCreadaEvent event) {
        log.info("Evento OrdenCreada recibido en ms-carrito para la orden ID: {} del usuario: {} ({})",
                event.getOrdenId(), event.getUsuarioNombre(), event.getUsuarioId());

        try {
            if (event.getUsuarioId() != null) {
                // CAMBIO CLAVE: Pasamos getUsuarioId() en lugar de getUsuarioCorreo()
                carritoService.vaciarCarritoPorUsuario(event.getUsuarioId(), event.getUsuarioNombre());
            } else {
                log.warn("El evento de orden creada no contiene usuarioId válido.");
            }
        } catch (Exception e) {
            log.error("Error al intentar vaciar el carrito para la orden ID {}: {}",
                    event.getOrdenId(), e.getMessage(), e);
        }
    }
}