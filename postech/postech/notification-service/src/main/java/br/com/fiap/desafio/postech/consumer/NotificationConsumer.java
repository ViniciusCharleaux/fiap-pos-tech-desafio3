package br.com.fiap.desafio.postech.consumer;

import br.com.fiap.desafio.postech.dto.NotificationMessage;
import br.com.fiap.desafio.postech.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void consume(NotificationMessage message) {
        log.info("Mensagem recebida da fila: {}", message);
        notificationService.processNotification(message);
    }
}