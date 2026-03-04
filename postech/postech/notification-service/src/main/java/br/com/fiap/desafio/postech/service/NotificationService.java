package br.com.fiap.desafio.postech.service;

import br.com.fiap.desafio.postech.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.enabled:false}")
    private boolean mailEnabled;

    public void processNotification(NotificationMessage message) {
        log.info("Notificacao recebida - Evento: {}, Paciente: {}, Email: {}, Medico: {}, Data: {}",
                message.getEventType(),
                message.getPatientName(),
                message.getPatientEmail(),
                message.getDoctorName(),
                message.getDateTime());

        if (mailEnabled) {
            sendEmail(message);
        } else {
            log.info("Envio de email desabilitado. Destinatario seria: {}", message.getPatientEmail());
        }
    }

    private void sendEmail(NotificationMessage message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(message.getPatientEmail());
            mail.setSubject(buildSubject(message));
            mail.setText(buildBody(message));
            mailSender.send(mail);
            log.info("Email enviado para {}", message.getPatientEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar email para {}: {}", message.getPatientEmail(), e.getMessage());
        }
    }

    private String buildSubject(NotificationMessage message) {
        return "CREATED".equals(message.getEventType())
                ? "Consulta agendada com sucesso"
                : "Consulta atualizada";
    }

    private String buildBody(NotificationMessage message) {
        return String.format(
                "Ola %s,\n\nSua consulta foi %s.\n\nMedico: %s\nData/Hora: %s\n\nAtenciosamente,\nHospital System",
                message.getPatientName(),
                "CREATED".equals(message.getEventType()) ? "agendada" : "atualizada",
                message.getDoctorName(),
                message.getDateTime()
        );
    }
}