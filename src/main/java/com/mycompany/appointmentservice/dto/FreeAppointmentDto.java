package com.mycompany.appointmentservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class FreeAppointmentDto {

    private final LocalDateTime start;

    private final LocalDateTime end;
}
