package br.com.fiap.desafio.postech.graphql;

import br.com.fiap.desafio.postech.dto.AppointmentDTO;
import br.com.fiap.desafio.postech.dto.AppointmentInput;
import br.com.fiap.desafio.postech.entity.User;
import br.com.fiap.desafio.postech.repository.UserRepository;
import br.com.fiap.desafio.postech.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_PATIENT')")
    public List<AppointmentDTO> appointmentsByPatient(@Argument String patientId) {
        return appointmentService.findByPatientId(Long.parseLong(patientId));
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_PATIENT')")
    public List<AppointmentDTO> futureAppointmentsByPatient(@Argument String patientId) {
        return appointmentService.findFutureByPatientId(Long.parseLong(patientId));
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_NURSE')")
    public List<AppointmentDTO> allAppointments() {
        return appointmentService.findAll();
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_PATIENT')")
    public AppointmentDTO appointmentById(@Argument String id) {
        return appointmentService.findById(Long.parseLong(id));
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT')")
    public List<AppointmentDTO> myAppointments() {
        User user = getAuthenticatedUser();
        return appointmentService.findByPatientId(user.getId());
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT')")
    public List<AppointmentDTO> myFutureAppointments() {
        User user = getAuthenticatedUser();
        return appointmentService.findFutureByPatientId(user.getId());
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_NURSE')")
    public AppointmentDTO createAppointment(@Argument AppointmentInput input) {
        return appointmentService.create(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_NURSE')")
    public AppointmentDTO updateAppointment(@Argument String id, @Argument AppointmentInput input) {
        return appointmentService.update(Long.parseLong(id), input);
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}