package com.mycompany.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DoctorDto {

    @NotNull(message = "doctorUUID cannot be null")
    private UUID doctorUUID;

    private String firstName;

    private String middleName;

    private String lastName;

    private List<WorkDayDto> workDays;
}
