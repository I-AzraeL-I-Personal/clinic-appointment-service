package com.mycompany.appointmentservice.service;

import com.mycompany.appointmentservice.dto.AppointmentDetailsDto;
import com.mycompany.appointmentservice.exception.DataInvalidException;
import com.mycompany.appointmentservice.exception.DataNotFoundException;
import com.mycompany.appointmentservice.repository.AppointmentDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentDetailsService {

    private final AppointmentDetailsRepository detailsRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Transactional
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public AppointmentDetailsDto updateDetails(Long id,
                                               AppointmentDetailsDto appointmentDetailsDto,
                                               Optional<MultipartFile> prescription,
                                               Optional<MultipartFile> attachment) {
        var appointmentDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
        appointmentDetails.setDescription(appointmentDetailsDto.getDescription());

        prescription.ifPresent(file -> {
            try {
                fileService.save(file, id.toString(), file.getOriginalFilename());
                appointmentDetails.setPrescriptionUri(file.getOriginalFilename());
            } catch (Exception e) {
                throw new DataInvalidException(id.toString(), "Prescription is invalid");
            }
        });
        attachment.ifPresent(file -> {
            try {
                fileService.save(file, id.toString(), file.getOriginalFilename());
                appointmentDetails.setAttachmentUri(file.getOriginalFilename());
            } catch (Exception e) {
                throw new DataInvalidException(id.toString(), "Attachment is invalid");
            }
        });

        return modelMapper.map(detailsRepository.save(appointmentDetails), AppointmentDetailsDto.class);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public AppointmentDetailsDto getDetails(Long id) {
        var appointmentDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
        return modelMapper.map(appointmentDetails, AppointmentDetailsDto.class);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public Resource getAttachmentResource(Long id) {
        var appointmentDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
        var name = appointmentDetails.getAttachmentUri();
        try {
            return fileService.load(id.toString(), name);
        } catch (Exception e) {
            throw new DataNotFoundException(id.toString(), "Attachment not found");
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@appointmentAccess.isOwnerOrDoctor(#id)")
    public Resource getPrescriptionResource(Long id) {
        var appointmentDetails = detailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id.toString(), "Appointment not found"));
        var name = appointmentDetails.getPrescriptionUri();
        try {
            return fileService.load(id.toString(), name);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataNotFoundException(id.toString(), "Prescription not found");
        }
    }
}
