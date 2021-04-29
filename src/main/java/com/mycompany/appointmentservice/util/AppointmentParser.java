package com.mycompany.appointmentservice.util;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.mycompany.appointmentservice.dto.FreeAppointmentDto;
import com.mycompany.appointmentservice.model.Appointment;
import com.mycompany.appointmentservice.model.AppointmentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class AppointmentParser {

    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;
    private final RangeSet<LocalTime> freeHours = TreeRangeSet.create();

    public AppointmentParser(LocalDate date, LocalTime start, LocalTime end) {
        this.date = date;
        this.start = start;
        this.end = end;
        freeHours.add(Range.closed(start, end));
    }

    public void subtractAll(List<Appointment> appointments) {
        appointments.forEach(this::subtract);
    }

    public void subtract(Appointment appointment) {
        freeHours.remove(Range.closed(appointment.getStartHour(), appointment.getEndHour()));
    }

    public List<FreeAppointmentDto> transformToDto(AppointmentType type) {
        RangeSet<LocalTime> parsedFreeHours = parseRangesToIntervals(freeHours, start, end, type.duration);
        return parsedFreeHours.asRanges().stream()
                .map(range -> new FreeAppointmentDto(
                        LocalDateTime.of(date, range.lowerEndpoint()),
                        LocalDateTime.of(date, range.upperEndpoint())))
                .collect(Collectors.toList());
    }

    private RangeSet<LocalTime> parseRangesToIntervals(RangeSet<LocalTime> ranges, LocalTime start, LocalTime end, int duration) {
        LocalTime parseHour = start;
        while (parseHour.compareTo(end) <= 0) {
            ranges.remove(Range.closed(parseHour, parseHour));
            parseHour = parseHour.plusMinutes(duration);
        }
        return ranges.asRanges().stream()
                .filter(range -> range.lowerEndpoint().until(range.upperEndpoint(), ChronoUnit.MINUTES) == duration)
                .collect(ImmutableRangeSet.toImmutableRangeSet());
    }
}
