package com.mycompany.appointmentservice.service;

import com.mycompany.appointmentservice.dto.DoctorDto;
import com.mycompany.appointmentservice.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorClient doctorClient;

    public DoctorDto getDoctorDto(UUID doctorUUID) {
        ResponseEntity<DoctorDto> doctorResponse = doctorClient.get(doctorUUID);
        if (doctorResponse.getStatusCode().isError() || doctorResponse.getBody() == null) {
            throw new DataNotFoundException(doctorUUID.toString(), "Doctor not found");
        }
        return doctorResponse.getBody();
    }

    public DoctorDto getDoctorDtoWithWorkdays(UUID doctorUUID) {
        ResponseEntity<DoctorDto> doctorResponse = doctorClient.getWithWorkdays(doctorUUID);
        if (doctorResponse.getStatusCode().isError() || doctorResponse.getBody() == null) {
            throw new DataNotFoundException(doctorUUID.toString(), "Doctor not found");
        }
        return doctorResponse.getBody();
    }
}
