package com.mycompany.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class WorkDayDto {

    private Long id;

    private DayOfWeek weekDay;

    private LocalTime startTime;

    private LocalTime endTime;
}
