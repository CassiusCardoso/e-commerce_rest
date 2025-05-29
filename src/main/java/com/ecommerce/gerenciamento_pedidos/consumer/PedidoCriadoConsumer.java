package com.ecommerce.gerenciamento_pedidos.consumer;

import com.ecommerce.gerenciamento_pedidos.Model.Pedido;
import com.ecommerce.gerenciamento_pedidos.Repository.PedidoRepository;
import com.ecommerce.gerenciamento_pedidos.Service.EmailService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class PedidoCriadoConsumer {
    private final EmailService emailService;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoCriadoConsumer(EmailService emailService, PedidoRepository pedidoRepository) {
        this.emailService = emailService;
        this.pedidoRepository = pedidoRepository;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.pedido-criado}")
    public void handleMessage(Long pedidoId) {
        System.out.println("Iniciando processamento de mensagem para pedido ID: " + pedidoId);
        try {
            System.out.println("1. Buscando pedido com ID: " + pedidoId);
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> {
                        System.err.println("Pedido com ID " + pedidoId + " não encontrado!");
                        return new IllegalArgumentException("Pedido com ID: " + pedidoId + " não encontrado.");
                    });
            System.out.println("2. Pedido encontrado: " + pedido);

            if (pedido.getUsuario() == null) {
                System.err.println("Nenhum usuário associado ao pedido ID: " + pedidoId);
                throw new AmqpRejectAndDontRequeueException("Nenhum usuário associado ao pedido ID: " + pedidoId);
            }

            String clienteEmail = pedido.getUsuario().getEmail();
            System.out.println("3. Email do cliente: " + clienteEmail);
            emailService.sendOrderConfirmationEmail(clienteEmail, pedidoId, pedido);
            System.out.println("5. Email enviado com sucesso para: " + clienteEmail);

        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao processar pedido (IllegalArgumentException): " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Erro ao processar pedido: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Erro ao enviar e-mail: " + e.getMessage(), e);
        } finally {
            System.out.println("Finalizando processamento de mensagem para pedido ID: " + pedidoId);
        }
    }
}