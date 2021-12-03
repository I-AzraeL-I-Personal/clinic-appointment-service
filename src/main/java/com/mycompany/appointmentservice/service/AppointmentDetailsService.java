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
import java.util.function.Consumer;

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
            uploadFile(prescription, "user/" + id.toString(), "prescription", appointmentDetails::setPrescriptionUri);
        }
        if (attachment != null) {
            uploadFile(attachment, "user/" + id.toString(), "attachment", appointmentDetails::setAttachmentUri);
        }

        return modelMapper.map(detailsRepository.save(appointmentDetails), AppointmentDetailsDto.class);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public AppointmentDetailsDto getDetails(Long id) {
        var appointmentDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
        return modelMapper.map(appointmentDetails, AppointmentDetailsDto.class);
    }

    private void uploadFile(MultipartFile file, String directory, String name, Consumer<String> callback) {
        try {
            var path = directory + "/" + name;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "auto",
                    "public_id", path
            ));
            callback.accept((String) response.get("secure_url"));
        } catch (IOException e) {
            log.error("IOException in uploadFile(): {}", e.getMessage());
            throw new DataInvalidException(name, "Couldn't upload file");
        }
    }
}
