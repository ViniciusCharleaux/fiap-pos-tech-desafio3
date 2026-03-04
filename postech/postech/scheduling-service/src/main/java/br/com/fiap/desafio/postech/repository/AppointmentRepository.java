package br.com.fiap.desafio.postech.repository;

import br.com.fiap.desafio.postech.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByPatientIdAndDateTimeAfter(Long patientId, LocalDateTime dateTime);
}