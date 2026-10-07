package com.backend.carrito.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String QUEUE_CARRITO_LIMPIAR = "q.limpiar-carrito";
    public static final String ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    public Queue limpiarCarritoQueue() {
        return QueueBuilder.durable(QUEUE_CARRITO_LIMPIAR).build();
    }

    @Bean
    public Binding bindingLimpiarCarrito(Queue limpiarCarritoQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(limpiarCarritoQueue)
                .to(pedidosExchange)
                .with(ROUTING_KEY_PEDIDO_CREADO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}