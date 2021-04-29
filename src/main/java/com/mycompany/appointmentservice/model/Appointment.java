package com.mycompany.appointmentservice.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "date cannot be null")
    private LocalDate date;

    @Column(nullable = false)
    @NotNull(message = "startHour cannot be null")
    private LocalTime startHour;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "appointType cannot be null")
    private AppointmentType type;

    @Column(nullable = false)
    @NotNull(message = "endHour cannot be null")
    private LocalTime endHour;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "doctorUUID", column = @Column(name = "doctor_uuid")),
            @AttributeOverride(name = "firstName", column = @Column(name = "doctor_first_name")),
            @AttributeOverride(name = "middleName", column = @Column(name = "doctor_middle_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "doctor_last_name")),
    })
    private Doctor doctor;

    @Column(nullable = false)
    @NotNull(message = "patientUUID cannot be null")
    private UUID patientUUID;

    public void calculateAndSetEndHour() {
        endHour = startHour.plusMinutes(type.duration);
    }
}
