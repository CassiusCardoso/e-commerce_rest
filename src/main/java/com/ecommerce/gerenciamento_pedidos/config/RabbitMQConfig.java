package com.ecommerce.gerenciamento_pedidos.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("docker")
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.queue.pedido-criado:pedido-criado-queue}")
    private String pedidoCriadoQueue;

    @Value("${spring.rabbitmq.exchange.pedido-criado:pedido-criado-exchange}")
    private String pedidoCriadoExchange;

    @Value("${spring.rabbitmq.routingkey.pedido-criado:pedido.criado.routingkey}")
    private String pedidoCriadoRoutingKey;

    @Bean
    public Queue pedidoCriadoQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "pedido-criado-dlq-exchange");
        args.put("x-dead-letter-routing-key", "pedido.criado.dlq.routingkey");
        return new Queue(pedidoCriadoQueue, true, false, false, args);
    }

    @Bean
    public Queue pedidoCriadoDlqQueue() {
        return new Queue("pedido-criado-dlq-queue", true);
    }

    @Bean
    public TopicExchange pedidoCriadoExchange() {
        return new TopicExchange(pedidoCriadoExchange);
    }

    @Bean
    public TopicExchange pedidoCriadoDlqExchange() {
        return new TopicExchange("pedido-criado-dlq-exchange");
    }

    @Bean
    public Binding binding(Queue pedidoCriadoQueue, TopicExchange pedidoCriadoExchange) {
        return BindingBuilder.bind(pedidoCriadoQueue)
                .to(pedidoCriadoExchange)
                .with(pedidoCriadoRoutingKey);
    }

    @Bean
    public Binding dlqBinding(Queue pedidoCriadoDlqQueue, TopicExchange pedidoCriadoDlqExchange) {
        return BindingBuilder.bind(pedidoCriadoDlqQueue)
                .to(pedidoCriadoDlqExchange)
                .with("pedido.criado.dlq.routingkey");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}