package com.mycompany.appointmentservice.controller;

import com.mycompany.appointmentservice.dto.AppointmentDto;
import com.mycompany.appointmentservice.dto.CreateAppointmentDto;
import com.mycompany.appointmentservice.dto.FreeAppointmentDto;
import com.mycompany.appointmentservice.model.AppointmentType;
import com.mycompany.appointmentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/find")
    public ResponseEntity<List<FreeAppointmentDto>> getFreeAppointmentsByDoctorAndDateAndType(
            @RequestParam UUID doctorUUID,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate,
            @RequestParam AppointmentType type) {
        return ResponseEntity.ok(appointmentService.findFreeAppointmentsByDoctorAndBetweenDatesAndType(doctorUUID, startDate, endDate, type));
    }

    @GetMapping("/patient/{patientUUID}")
    public Collection<AppointmentDto> getAllByPatientUUID(@PathVariable UUID patientUUID) {
        return appointmentService.getAllByPatientUUID(patientUUID);
    }

    @GetMapping("/doctor/{doctorUUID}")
    public Collection<AppointmentDto> getAllByDoctorUUID(@PathVariable UUID doctorUUID) {
        return appointmentService.getAllByDoctorUUID(doctorUUID);
    }

    @PostMapping("")
    public ResponseEntity<AppointmentDto> create(@Valid @RequestBody CreateAppointmentDto appointmentDto) {
        return ResponseEntity.ok(appointmentService.create(appointmentDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
