package br.com.fiap.desafio.postech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentInput {

    @NotNull
    private String patientId;

    @NotBlank
    private String patientName;

    @NotBlank
    private String patientEmail;

    @NotBlank
    private String doctorName;

    @NotBlank
    private String dateTime;

    private String description;

    private String status;
}