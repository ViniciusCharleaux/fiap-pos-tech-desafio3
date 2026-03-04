package br.com.fiap.desafio.postech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {
    private Long appointmentId;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private String dateTime;
    private String eventType;
}