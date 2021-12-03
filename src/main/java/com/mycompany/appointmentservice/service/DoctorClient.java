package com.mycompany.appointmentservice.service;

import com.mycompany.appointmentservice.dto.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "${app.doctorServiceName}")
public interface DoctorClient {

    @GetMapping(value = "/{uuid}")
    ResponseEntity<DoctorDto> get(@PathVariable UUID uuid);

    @GetMapping(value = "/{uuid}/with-contact")
    ResponseEntity<DoctorDto> getWithContact(@PathVariable UUID uuid);

    @GetMapping(value = "/{uuid}/with-workdays")
    ResponseEntity<DoctorDto> getWithWorkdays(@PathVariable UUID uuid);

    @GetMapping(value = "/{uuid}/with-contact-and-workdays")
    ResponseEntity<DoctorDto> getWithContactAndWorkdays(@PathVariable UUID uuid);
}
