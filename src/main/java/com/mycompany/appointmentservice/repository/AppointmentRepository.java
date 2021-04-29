package com.mycompany.appointmentservice.repository;

import com.mycompany.appointmentservice.model.Appointment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByDoctor_DoctorUUIDAndDateBetween(UUID doctorUUID,
                                                               LocalDate startDate,
                                                               LocalDate endDate,
                                                               Sort sort);

    List<Appointment> findAllByPatientUUIDOrderByDateDescStartHourAsc(UUID patientUUID);

    List<Appointment> findAllByDoctor_DoctorUUIDOrderByDateDescStartHourAsc(UUID doctorUUID);

    boolean existsByDateAndStartHourAndDoctor_DoctorUUID(LocalDate date, LocalTime startTime, UUID doctorUUID);
}
