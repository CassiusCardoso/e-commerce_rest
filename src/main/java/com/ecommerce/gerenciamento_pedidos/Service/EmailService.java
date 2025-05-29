package com.ecommerce.gerenciamento_pedidos.Service;

import com.ecommerce.gerenciamento_pedidos.Model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendOrderConfirmationEmail(String to, Long pedidoId, Pedido pedido) {
        if (to == null || to.isEmpty()) {
            throw new IllegalArgumentException("E-mail do destinatário não pode ser nulo ou vazio");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Confirmação de Pedido | E-commerce \uD83D\uDD14");
        message.setText("Olá! Seu pedido com ID " + pedidoId + " foi criado com sucesso. Agradecemos pela compra!");
        mailSender.send(message);
    }
}