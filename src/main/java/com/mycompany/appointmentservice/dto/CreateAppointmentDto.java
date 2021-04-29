package com.mycompany.appointmentservice.dto;

import com.mycompany.appointmentservice.model.AppointmentType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAppointmentDto {

    @NotNull(message = "date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "date must be future or present value")
    private LocalDate date;

    @NotNull(message = "startHour cannot be null")
    private LocalTime startHour;

    @NotNull(message = "appointmentType cannot be null")
    private AppointmentType type;

    @NotNull(message = "patientUUID cannot be null")
    private UUID patientUUID;

    @NotNull(message = "doctorUUID cannot be null")
    private UUID doctorUUID;
}
