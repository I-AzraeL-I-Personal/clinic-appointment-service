package com.mycompany.appointmentservice.util;

import com.mycompany.appointmentservice.dto.WorkDayDto;
import com.mycompany.appointmentservice.model.Appointment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class AppointmentValidator {

    @Value("${app.usedTimezone}")
    private String usedTimezone;

    public boolean isValid(WorkDayDto workDay, Appointment appointment) {
        return isTimeAfterStart(workDay.getStartTime(), appointment)
                && isTimeBeforeEnd(workDay.getEndTime(), appointment)
                && isTimeNotPast(appointment)
                && isTimeProper(appointment);
    }

    private boolean isTimeAfterStart(LocalTime startTime, Appointment appointment) {
        return startTime.compareTo(appointment.getStartHour()) <= 0;
    }

    private boolean isTimeBeforeEnd(LocalTime endTime, Appointment appointment) {
        return endTime.minusMinutes(appointment.getType().duration).compareTo(appointment.getEndHour()) >= 0;
    }

    private boolean isTimeNotPast(Appointment appointment) {
        return LocalDateTime.of(appointment.getDate(), appointment.getStartHour()).isAfter(LocalDateTime.now(ZoneId.of(usedTimezone)));
    }

    private boolean isTimeProper(Appointment appointment) {
        return appointment.getStartHour().getMinute() % appointment.getType().duration == 0;
    }
}
