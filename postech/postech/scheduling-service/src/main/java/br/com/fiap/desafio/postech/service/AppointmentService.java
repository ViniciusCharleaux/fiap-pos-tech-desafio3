package br.com.fiap.desafio.postech.service;

import br.com.fiap.desafio.postech.dto.AppointmentDTO;
import br.com.fiap.desafio.postech.dto.AppointmentInput;
import br.com.fiap.desafio.postech.dto.NotificationMessage;
import br.com.fiap.desafio.postech.entity.Appointment;
import br.com.fiap.desafio.postech.entity.AppointmentStatus;
import br.com.fiap.desafio.postech.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<AppointmentDTO> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AppointmentDTO> findFutureByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdAndDateTimeAfter(patientId, LocalDateTime.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AppointmentDTO> findAll() {
        return appointmentRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AppointmentDTO findById(Long id) {
        return appointmentRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public AppointmentDTO create(AppointmentInput input) {
        Appointment appointment = Appointment.builder()
                .patientId(input.getPatientId())
                .patientName(input.getPatientName())
                .patientEmail(input.getPatientEmail())
                .doctorName(input.getDoctorName())
                .dateTime(LocalDateTime.parse(input.getDateTime(), FORMATTER))
                .description(input.getDescription())
                .status(input.getStatus() != null ? AppointmentStatus.valueOf(input.getStatus()) : AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        sendNotification(saved, "CREATED");
        return toDTO(saved);
    }

    public AppointmentDTO update(Long id, AppointmentInput input) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));

        appointment.setPatientId(input.getPatientId());
        appointment.setPatientName(input.getPatientName());
        appointment.setPatientEmail(input.getPatientEmail());
        appointment.setDoctorName(input.getDoctorName());
        appointment.setDateTime(LocalDateTime.parse(input.getDateTime(), FORMATTER));
        appointment.setDescription(input.getDescription());
        if (input.getStatus() != null) {
            appointment.setStatus(AppointmentStatus.valueOf(input.getStatus()));
        }

        Appointment saved = appointmentRepository.save(appointment);
        sendNotification(saved, "UPDATED");
        return toDTO(saved);
    }

    private void sendNotification(Appointment appointment, String eventType) {
        NotificationMessage message = NotificationMessage.builder()
                .appointmentId(appointment.getId())
                .patientName(appointment.getPatientName())
                .patientEmail(appointment.getPatientEmail())
                .doctorName(appointment.getDoctorName())
                .dateTime(appointment.getDateTime().format(FORMATTER))
                .eventType(eventType)
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    private AppointmentDTO toDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(appointment.getPatientName())
                .patientEmail(appointment.getPatientEmail())
                .doctorName(appointment.getDoctorName())
                .dateTime(appointment.getDateTime())
                .description(appointment.getDescription())
                .status(appointment.getStatus())
                .build();
    }
}