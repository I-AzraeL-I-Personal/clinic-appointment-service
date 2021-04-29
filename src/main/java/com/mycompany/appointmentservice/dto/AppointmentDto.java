package com.mycompany.appointmentservice.dto;

import com.mycompany.appointmentservice.model.AppointmentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class AppointmentDto {

    private Long id;

    private LocalDate date;

    private LocalTime startHour;

    private LocalTime endHour;

    private AppointmentType type;

    private UUID patientUUID;

    private DoctorDto doctor;
}
