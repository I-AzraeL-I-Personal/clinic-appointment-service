package com.mycompany.appointmentservice.controller;

import com.mycompany.appointmentservice.dto.AppointmentDetailsDto;
import com.mycompany.appointmentservice.service.AppointmentDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AppointmentDetailsController {

    private final AppointmentDetailsService detailsService;

    @GetMapping("/{id}/details")
    public ResponseEntity<AppointmentDetailsDto> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(detailsService.getDetails(id));
    }

    @PostMapping("/{id}/details")
    public ResponseEntity<AppointmentDetailsDto> update(@RequestPart("details") AppointmentDetailsDto details,
                                                        @RequestPart(value = "prescription", required = false) MultipartFile prescription,
                                                        @RequestPart(value = "attachment", required = false) MultipartFile attachment,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(detailsService.updateDetails(id, details, prescription, attachment));
    }
}
