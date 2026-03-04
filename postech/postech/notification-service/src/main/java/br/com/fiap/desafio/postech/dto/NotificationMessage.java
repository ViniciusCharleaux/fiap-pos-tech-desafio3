package br.com.fiap.desafio.postech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Long appointmentId;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private String dateTime;
    private String eventType;
}