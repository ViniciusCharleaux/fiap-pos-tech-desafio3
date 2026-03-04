package br.com.fiap.desafio.postech.graphql;

import br.com.fiap.desafio.postech.dto.AppointmentDTO;
import br.com.fiap.desafio.postech.dto.AppointmentInput;
import br.com.fiap.desafio.postech.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    public List<AppointmentDTO> appointmentsByPatient(@Argument Long patientId) {
        return appointmentService.findByPatientId(patientId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    public List<AppointmentDTO> futureAppointmentsByPatient(@Argument Long patientId) {
        return appointmentService.findFutureByPatientId(patientId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public List<AppointmentDTO> allAppointments() {
        return appointmentService.findAll();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    public AppointmentDTO appointmentById(@Argument Long id) {
        return appointmentService.findById(id);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public AppointmentDTO createAppointment(@Argument AppointmentInput input) {
        return appointmentService.create(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public AppointmentDTO updateAppointment(@Argument Long id, @Argument AppointmentInput input) {
        return appointmentService.update(id, input);
    }
}