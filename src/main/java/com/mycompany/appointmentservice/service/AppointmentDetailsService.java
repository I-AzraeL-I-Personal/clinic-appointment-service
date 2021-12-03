package com.mycompany.appointmentservice.service;

import com.cloudinary.Cloudinary;
import com.mycompany.appointmentservice.dto.AppointmentDetailsDto;
import com.mycompany.appointmentservice.exception.DataInvalidException;
import com.mycompany.appointmentservice.exception.DataNotFoundException;
import com.mycompany.appointmentservice.repository.AppointmentDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentDetailsService {

    private final AppointmentDetailsRepository detailsRepository;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    @Transactional
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public AppointmentDetailsDto updateDetails(Long id,
                                               AppointmentDetailsDto appointmentDetailsDto,
                                               @RequestParam(required = false) MultipartFile prescription,
                                               @RequestParam(required = false) MultipartFile attachment) {
        var appointmentDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
        appointmentDetails.setDescription(appointmentDetailsDto.getDescription());

        if (prescription != null) {
            Map<String, Object> response = uploadFile(prescription, "user/" + id.toString(), "prescription");
            if (response != null) {
                var url = (String) response.get("secure_url");
                var format = (String) response.get("format");
                appointmentDetails.setPrescription(url);
                appointmentDetails.setPrescriptionFormat(format);
            } else {
                appointmentDetails.setPrescription(null);
            }
        }
        if (attachment != null) {
            Map<String, Object> response = uploadFile(attachment, "user/" + id.toString(), "attachment");
            if (response != null) {
                var url = (String) response.get("secure_url");
                var format = (String) response.get("format");
                appointmentDetails.setAttachment(url);
                appointmentDetails.setAttachmentFormat(format);
            } else {
                appointmentDetails.setAttachment(null);
            }
        }

        return modelMapper.map(appointmentDetails, AppointmentDetailsDto.class);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public AppointmentDetailsDto getDetails(Long id) {
        return detailsRepository.findById(id)
                .map(details -> modelMapper.map(details, AppointmentDetailsDto.class))
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
    }

    private Map<String, Object> uploadFile(MultipartFile file, String directory, String name) {
        try {
            var path = directory + "/" + name;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "raw",
                    "type", "upload",
                    "public_id", path));
            return response;
        } catch (IOException e) {
            log.error("Exception in uploadFile(): {}", e.getMessage());
            throw new DataInvalidException(name, "Couldn't upload file");
        }
    }
}
