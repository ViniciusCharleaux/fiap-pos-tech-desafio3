package br.com.fiap.desafio.postech.dto;

import br.com.fiap.desafio.postech.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private LocalDateTime dateTime;
    private String description;
    private AppointmentStatus status;
}